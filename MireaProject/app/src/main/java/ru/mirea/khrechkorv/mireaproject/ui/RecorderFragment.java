package ru.mirea.khrechkorv.mireaproject.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;

import ru.mirea.khrechkorv.mireaproject.R;

public class RecorderFragment extends Fragment {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private Button btnRecord, btnPlay, btnDelete;
    private TextView tvRecordingStatus;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;
    private boolean isRecording = false;
    private boolean isPlaying = false;
    private String recordFilePath = null;
    private File audioFile;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recorder, container, false);

        btnRecord = root.findViewById(R.id.btnRecord);
        btnPlay = root.findViewById(R.id.btnPlay);
        btnDelete = root.findViewById(R.id.btnDelete);
        tvRecordingStatus = root.findViewById(R.id.tvRecordingStatus);

        setupAudioFilePath();

        btnRecord.setOnClickListener(view -> handleRecordButtonClick());
        btnPlay.setOnClickListener(view -> handlePlayButtonClick());
        btnDelete.setOnClickListener(view -> deleteAudio());

        checkExistingRecording();

        return root;
    }

    private void setupAudioFilePath() {
        File musicDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        if (musicDir != null) {
            if (!musicDir.exists()) {
                musicDir.mkdirs();
            }
            audioFile = new File(musicDir, "my_recording.3gp");
            recordFilePath = audioFile.getAbsolutePath();
            Log.d("RecorderFragment", "Recording file path: " + recordFilePath);
        }
    }

    private void checkExistingRecording() {
        if (audioFile != null && audioFile.exists()) {
            btnPlay.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            btnPlay.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
        }
    }

    private void handleRecordButtonClick() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            if (isRecording) {
                stopRecording();
            } else {
                startRecording();
            }
        }
    }

    private void handlePlayButtonClick() {
        if (isPlaying) {
            stopPlaying();
        } else {
            startPlaying();
        }
    }

    private void startRecording() {
        if (recordFilePath == null) {
            Toast.makeText(getContext(), "Ошибка: путь для сохранения не найден", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isPlaying) {
            stopPlaying();
        }

        if (audioFile.exists()) {
            boolean deleted = audioFile.delete();
            Log.d("RecorderFragment", "Old recording deleted: " + deleted);
        }

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(recordFilePath);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
            recorder.start();
            isRecording = true;

            // Обновляем UI
            btnRecord.setText("Остановить запись");
            btnPlay.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
            tvRecordingStatus.setVisibility(View.VISIBLE);
            tvRecordingStatus.setText("Идет запись...");

            Toast.makeText(getContext(), "Запись начата", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Log.e("RecorderFragment", "Ошибка при подготовке записи: " + e.getMessage());
            Toast.makeText(getContext(), "Ошибка при начале записи", Toast.LENGTH_SHORT).show();
            if (recorder != null) {
                recorder.release();
                recorder = null;
            }
        }
    }

    private void stopRecording() {
        if (recorder != null) {
            try {
                recorder.stop();
                recorder.release();
                recorder = null;
                isRecording = false;

                Toast.makeText(getContext(), "Запись сохранена", Toast.LENGTH_SHORT).show();

                btnRecord.setText("Начать запись");
                btnPlay.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.VISIBLE);
                tvRecordingStatus.setVisibility(View.GONE);

            } catch (RuntimeException e) {
                Log.e("RecorderFragment", "Ошибка при остановке записи: " + e.getMessage());
                Toast.makeText(getContext(), "Ошибка при остановке записи", Toast.LENGTH_SHORT).show();

                if (audioFile.exists()) {
                    audioFile.delete();
                }
            }
        }
    }

    private void startPlaying() {
        if (recordFilePath == null || !audioFile.exists()) {
            Toast.makeText(getContext(), "Файл не найден", Toast.LENGTH_SHORT).show();
            return;
        }

        stopPlaying();

        player = new MediaPlayer();
        try {
            player.setDataSource(recordFilePath);
            player.prepare();
            player.start();
            isPlaying = true;
            btnPlay.setText("Пауза");

            player.setOnCompletionListener(mp -> {
                stopPlaying();
                Toast.makeText(getContext(), "Воспроизведение завершено", Toast.LENGTH_SHORT).show();
            });

            player.setOnErrorListener((mp, what, extra) -> {
                Toast.makeText(getContext(), "Ошибка воспроизведения", Toast.LENGTH_SHORT).show();
                stopPlaying();
                return true;
            });

        } catch (IOException e) {
            Log.e("RecorderFragment", "Ошибка при воспроизведении: " + e.getMessage());
            Toast.makeText(getContext(), "Ошибка при воспроизведении", Toast.LENGTH_SHORT).show();
            stopPlaying();
        }
    }

    private void stopPlaying() {
        if (player != null) {
            if (player.isPlaying()) {
                player.stop();
            }
            player.release();
            player = null;
        }
        isPlaying = false;
        btnPlay.setText("Воспроизвести");
    }

    private void deleteAudio() {
        if (audioFile != null && audioFile.exists()) {
            if (isPlaying) {
                stopPlaying();
            }

            if (audioFile.delete()) {
                Toast.makeText(getContext(), "Аудио удалено", Toast.LENGTH_SHORT).show();
                btnPlay.setVisibility(View.GONE);
                btnDelete.setVisibility(View.GONE);
            } else {
                Toast.makeText(getContext(), "Ошибка при удалении файла", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Файл не существует", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Разрешение получено", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Разрешение на запись аудио необходимо", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            try {
                if (isRecording) {
                    recorder.stop();
                }
                recorder.release();
            } catch (Exception e) {
                Log.e("RecorderFragment", "Ошибка при освобождении рекордера: " + e.getMessage());
            }
            recorder = null;
        }
        if (player != null) {
            player.release();
            player = null;
        }
    }
}

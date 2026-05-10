package ru.mirea.khrechkorv.mireaproject.ui.FileConverter;

import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.mirea.khrechkorv.mireaproject.R;

public class FileFragment extends Fragment implements FileAdapter.OnFileActionListener {

    private RecyclerView recyclerView;
    private FileAdapter adapter;
    private List<FileItem> fileList = new ArrayList<>();
    private ImageButton addFile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_files, container, false);

        recyclerView = root.findViewById(R.id.recyclerViewFiles);
        addFile = root.findViewById(R.id.imageButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadFiles();

        adapter = new FileAdapter(fileList,  this);
        recyclerView.setAdapter(adapter);

        addFile.setOnClickListener(v -> showCreateFileDialog());

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFiles();
        adapter.notifyDataSetChanged();
    }

    private void loadFiles() {
        fileList.clear();
        File dir = requireContext().getFilesDir();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    fileList.add(new FileItem(file.getName(), file.length(), file.lastModified(), file));
                }
            }
        }
    }

    /**
     * Диалоговое окно создания файла с выбором формата
     */
    public void showCreateFileDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_create_file, null);
        EditText etName = dialogView.findViewById(R.id.editTextFileName);
        EditText etContent = dialogView.findViewById(R.id.editTextFileContent);
        RadioGroup rgFileType = dialogView.findViewById(R.id.radioGroupFileType);

        new AlertDialog.Builder(getContext())
                .setTitle("Создать файл")
                .setView(dialogView)
                .setPositiveButton("Создать", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String content = etContent.getText().toString();
                    int selectedType = rgFileType.getCheckedRadioButtonId();

                    if (TextUtils.isEmpty(name)) {
                        Toast.makeText(getContext(), "Введите имя файла", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String extension = "";
                    if (selectedType == R.id.radioTxt) {
                        extension = ".txt";
                    } else if (selectedType == R.id.radioJson) {
                        extension = ".json";
                    } else if (selectedType == R.id.radioXml) {
                        extension = ".xml";
                    }

                    String fileName = name + extension;
                    String formattedContent = formatContentByType(content, extension);
                    createFile(fileName, formattedContent);
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    /**
     * Форматирование содержимого в зависимости от типа файла
     */
    private String formatContentByType(String content, String extension) {
        switch (extension) {
            case ".json":
                try {
                    JSONObject json = new JSONObject();
                    json.put("content", content);
                    json.put("createdAt", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
                    return json.toString(2);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return "{\"content\": \"" + content.replace("\"", "\\\"") + "\"}";
                }
            case ".xml":
                return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<root>\n    <content>" +
                        escapeXml(content) + "</content>\n</root>";
            default:
                return content;
        }
    }

    private String escapeXml(String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private void createFile(String fileName, String content) {
        try (FileOutputStream fos = requireContext().openFileOutput(fileName, Context.MODE_PRIVATE)) {
            fos.write(content.getBytes(StandardCharsets.UTF_8));
            loadFiles();
            adapter.notifyDataSetChanged();
            Toast.makeText(getContext(), "Файл '" + fileName + "' создан", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Ошибка создания файла: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Конвертация файлов между форматами
     */
    private void convertFile(FileItem fileItem) {
        File file = fileItem.getFile();
        String fileName = file.getName();

        // Определяем текущий формат и целевой
        String currentFormat = getFileExtension(fileName);
        String targetFormat = getTargetFormat(currentFormat);

        if (targetFormat == null) {
            Toast.makeText(getContext(), "Невозможно конвертировать формат: " + currentFormat, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String content = readFileContent(file);
            String newFileName = fileName.replace(currentFormat, targetFormat);
            String newContent = convertContent(content, currentFormat, targetFormat);

            File newFile = new File(requireContext().getFilesDir(), newFileName);
            try (FileOutputStream fos = new FileOutputStream(newFile)) {
                fos.write(newContent.getBytes(StandardCharsets.UTF_8));
            }

            loadFiles();
            adapter.notifyDataSetChanged();
            Toast.makeText(getContext(), "Конвертировано: " + fileName + " → " + newFileName, Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Ошибка конвертации: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf(".");
        if (lastDot > 0) {
            return fileName.substring(lastDot);
        }
        return "";
    }

    private String getTargetFormat(String currentFormat) {
        switch (currentFormat) {
            case ".txt": return ".json";
            case ".json": return ".xml";
            case ".xml": return ".txt";
            default: return null;
        }
    }

    private String readFileContent(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString().trim();
    }

    private String convertContent(String content, String fromFormat, String toFormat) {
        if (fromFormat.equals(".txt") && toFormat.equals(".json")) {
            // TXT → JSON
            try {
                JSONObject json = new JSONObject();
                json.put("content", content);
                json.put("convertedFrom", "txt");
                json.put("convertedAt", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
                return json.toString(2);
            } catch (JSONException e) {
                return "{\"content\": \"" + content.replace("\"", "\\\"") + "\"}";
            }
        }
        else if (fromFormat.equals(".json") && toFormat.equals(".xml")) {
            // JSON → XML
            try {
                JSONObject json = new JSONObject(content);
                String jsonContent = json.optString("content", content);
                StringBuilder xml = new StringBuilder();
                xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                xml.append("<root>\n");
                xml.append("    <content>").append(escapeXml(jsonContent)).append("</content>\n");
                xml.append("    <convertedFrom>json</convertedFrom>\n");
                xml.append("</root>");
                return xml.toString();
            } catch (JSONException e) {
                return "<?xml version=\"1.0\"?>\n<root>\n    <content>" + escapeXml(content) + "</content>\n</root>";
            }
        }
        else if (fromFormat.equals(".xml") && toFormat.equals(".txt")) {
            // XML → TXT - извлекаем содержимое между тегами <content>
            int startTag = content.indexOf("<content>");
            int endTag = content.indexOf("</content>");
            if (startTag != -1 && endTag != -1) {
                return content.substring(startTag + 9, endTag).trim();
            }
            return content.replaceAll("<[^>]*>", "").trim();
        }
        return content;
    }

    /**
     * Удаление файла
     */
    private void deleteFile(FileItem fileItem) {
        new AlertDialog.Builder(getContext())
                .setTitle("Удалить файл")
                .setMessage("Вы уверены, что хотите удалить файл '" + fileItem.getFileName() + "'?")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    if (fileItem.getFile().delete()) {
                        loadFiles();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Файл удален", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Ошибка удаления файла", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    /**
     * Просмотр содержимого файла
     */
    private void viewFileContent(FileItem fileItem) {
        try {
            String content = readFileContent(fileItem.getFile());

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Содержимое: " + fileItem.getFileName());

            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_file_content, null);
            EditText etContent = dialogView.findViewById(R.id.editTextFileContentPreview);
            etContent.setText(content);
            etContent.setEnabled(false);

            builder.setView(dialogView)
                    .setPositiveButton("Закрыть", null)
                    .setNeutralButton("Редактировать", (dialog, which) -> showEditFileDialog(fileItem))
                    .show();

        } catch (IOException e) {
            Toast.makeText(getContext(), "Ошибка чтения файла", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Редактирование файла
     */
    private void showEditFileDialog(FileItem fileItem) {
        try {
            String content = readFileContent(fileItem.getFile());

            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_file_content, null);
            EditText etContent = dialogView.findViewById(R.id.editTextFileContentPreview);
            etContent.setText(content);
            etContent.setEnabled(true);

            new AlertDialog.Builder(getContext())
                    .setTitle("Редактировать: " + fileItem.getFileName())
                    .setView(dialogView)
                    .setPositiveButton("Сохранить", (dialog, which) -> {
                        String newContent = etContent.getText().toString();
                        saveFileContent(fileItem.getFile(), newContent);
                    })
                    .setNegativeButton("Отмена", null)
                    .show();
        } catch (IOException e) {
            Toast.makeText(getContext(), "Ошибка загрузки файла", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveFileContent(File file, String content) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content.getBytes(StandardCharsets.UTF_8));
            Toast.makeText(getContext(), "Файл сохранен", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getContext(), "Ошибка сохранения", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConvert(FileItem fileItem) {
        convertFile(fileItem);
    }

    @Override
    public void onDelete(FileItem fileItem) {
        deleteFile(fileItem);
    }

    @Override
    public void onView(FileItem fileItem) {
        viewFileContent(fileItem);
    }
}

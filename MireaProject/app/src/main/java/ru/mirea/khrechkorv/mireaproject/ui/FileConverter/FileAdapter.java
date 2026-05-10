package ru.mirea.khrechkorv.mireaproject.ui.FileConverter;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import ru.mirea.khrechkorv.mireaproject.R;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

    private List<FileItem> fileList;
    private OnFileActionListener listener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

    public interface OnFileActionListener {
        void onConvert(FileItem fileItem);
        void onDelete(FileItem fileItem);
        void onView(FileItem fileItem);
    }

    public FileAdapter(List<FileItem> fileList, OnFileActionListener listener) {
        this.fileList = fileList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FileItem fileItem = fileList.get(position);

        holder.tvFileName.setText(fileItem.getFileName());
        holder.tvFileSize.setText(formatFileSize(fileItem.getFileSize()));
        holder.tvModified.setText(dateFormat.format(fileItem.getModifiedDate()));


        holder.btnConvert.setOnClickListener(v -> listener.onConvert(fileItem));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(fileItem));
        holder.itemView.setOnClickListener(v -> listener.onView(fileItem));
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format(Locale.getDefault(), "%.1f KB", size / 1024.0);
        } else {
            return String.format(Locale.getDefault(), "%.1f MB", size / (1024.0 * 1024.0));
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFileIcon;
        TextView tvFileName;
        TextView tvFileSize;
        TextView tvModified;
        ImageView btnConvert;
        ImageView btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            ivFileIcon = itemView.findViewById(R.id.ivFileIcon);
            tvFileName = itemView.findViewById(R.id.tvFileName);
            tvFileSize = itemView.findViewById(R.id.tvFileSize);
            tvModified = itemView.findViewById(R.id.tvModified);
            btnConvert = itemView.findViewById(R.id.btnConvert);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}

package com.example.twittersearch;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String SEARCHES = "searches";//文件的名称
    private EditText queryEditText;// 搜索查询文本
    private EditText tagEditText;//标签文本框
    private FloatingActionButton saveFloatingActionButton;//保存按钮
    private SharedPreferences savedSearches;//保存搜索查询和标签的SharedPreferences
    private List<String> tags;//保存的搜索标签列表
    private SearchesAdapter adapter;//适配器，用于填充Recyclerview

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置toolbar
        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //初始化搜索查询和标签文本框
        queryEditText = ((TextInputLayout) findViewById(
                R.id.queryTextInputLayout)).getEditText();
        queryEditText.addTextChangedListener(textWatcher);
        tagEditText = ((TextInputLayout) findViewById(
                R.id.tagTextInputLayout)).getEditText();
        tagEditText.addTextChangedListener(textWatcher);

        savedSearches = getSharedPreferences(SEARCHES, MODE_PRIVATE);
        tags = new ArrayList<>(savedSearches.getAll().keySet());
        Collections.sort(tags, String.CASE_INSENSITIVE_ORDER);
        //设置RecyclerView和适配器
        RecyclerView recyclerView =
                (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchesAdapter(
                tags, itemClickListener, itemLongClickListener);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new ItemDivider(this));

        saveFloatingActionButton =
                (FloatingActionButton) findViewById(R.id.fab);
        saveFloatingActionButton.setOnClickListener(saveButtonListener);
        updateSaveFAB();
    }

    // TextWatcher 用于监听搜索框和标签框的输入变化,并更新保存按钮的可见性
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Do nothing
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            updateSaveFAB();
        }
        @Override
        public void afterTextChanged(Editable s) {
            // Do nothing
        }
    };

    // 根据搜索框和标签框的内容,更新保存按钮的可见性
    private void updateSaveFAB() {
        if (queryEditText.getText().toString().isEmpty() ||
                tagEditText.getText().toString().isEmpty())
            saveFloatingActionButton.hide();
        else
            saveFloatingActionButton.show();
    }
    // 保存按钮的点击事件处理
    private final View.OnClickListener saveButtonListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 获取搜索查询和标签,保存到 SharedPreferences 中
                    String query = queryEditText.getText().toString();
                    String tag = tagEditText.getText().toString();
                    // 如果标签不存在,则添加到标签列表并更新适配器
                    if (!query.isEmpty() && !tag.isEmpty()) {
                        ((InputMethodManager) getSystemService(
                                Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                                view.getWindowToken(), 0);
                        addTaggedSearch(tag, query);
                        // 清空搜索框和标签框,并将焦点置于搜索框
                        queryEditText.setText("");
                        tagEditText.setText("");
                        queryEditText.requestFocus();
                    }
                }
            };

    // 添加新的搜索查询和标签到 SharedPreferences 和标签列表中
    private void addTaggedSearch(String tag, String query) {
        SharedPreferences.Editor preferencesEditor = savedSearches.edit();
        preferencesEditor.putString(tag, query);
        preferencesEditor.apply();

        if (!tags.contains(tag)) {
            tags.add(tag);
            Collections.sort(tags, String.CASE_INSENSITIVE_ORDER);
            adapter.notifyDataSetChanged();
        }
    }


    // 列表项点击事件处理,打开对应的 Twitter 搜索页面
    private final View.OnClickListener itemClickListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 获取点击的标签,构建 Twitter 搜索 URL,并打开浏览器
                    String tag = ((TextView) view).getText().toString();
                    String urlString = getString(R.string.search_URL) +
                            Uri.encode(savedSearches.getString(tag, ""), "UTF-8");
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(urlString));
                    startActivity(webIntent);
                }
            };


    // 列表项长按事件处理,弹出分享、编辑或删除选项
    private final View.OnLongClickListener itemLongClickListener =
            new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    // 获取长按的标签,弹出对话框,提供分享、编辑或删除选项
                    final String tag = ((TextView) view).getText().toString();
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(getString(R.string.share_edit_delete_title, tag));
                    builder.setItems(R.array.dialog_items,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0: // share
                                            shareSearch(tag);
                                            break;
                                        case 1: // edit
                                            tagEditText.setText(tag);
                                            queryEditText.setText(
                                                    savedSearches.getString(tag, ""));
                                            break;
                                        case 2: // delete
                                            deleteSearch(tag);
                                            break;
                                    }
                                }
                            });
                    builder.setNegativeButton(getString(R.string.cancel), null);
                    builder.create().show();
                    return true;
                }
            };

    // 分享当前搜索
    private void shareSearch(String tag) {
        String urlString = getString(R.string.search_URL) +
                Uri.encode(savedSearches.getString(tag, ""), "UTF-8");
        // 构建分享 Intent,并启动
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT,
                getString(R.string.share_subject));
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                getString(R.string.share_message, urlString));
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent,
                getString(R.string.share_search)));
    }

    // 删除指定标签对应的搜索
    private void deleteSearch(final String tag) {
        // 弹出确认对话框,获得用户确认后,从 SharedPreferences 和标签列表中删除
        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(this);
        confirmBuilder.setMessage(getString(R.string.confirm_message, tag));
        confirmBuilder.setNegativeButton(getString(R.string.cancel), null);
        confirmBuilder.setPositiveButton(getString(R.string.delete),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        tags.remove(tag);
                        SharedPreferences.Editor preferencesEditor =
                                savedSearches.edit();
                        preferencesEditor.remove(tag);
                        preferencesEditor.apply();
                        adapter.notifyDataSetChanged();
                    }
                });
        confirmBuilder.create().show();
    }
}

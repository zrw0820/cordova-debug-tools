package com.mysoft.debug_tools.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evrencoskun.tableview.adapter.AbstractTableAdapter;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.mysoft.debug_tools.R;

/**
 * Created by Zourw on 2018/9/18.
 */
public class TableAdapter extends AbstractTableAdapter<String, String, String> {
    private Context context;

    public TableAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public int getColumnHeaderItemViewType(int position) {
        return 0;
    }

    @Override
    public int getRowHeaderItemViewType(int position) {
        return 0;
    }

    @Override
    public int getCellItemViewType(int position) {
        return 0;
    }

    static class CellViewHolder extends AbstractViewHolder {
        TextView text;

        CellViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
        }
    }

    static class ColumnHeaderViewHolder extends AbstractViewHolder {
        TextView text;

        ColumnHeaderViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
        }
    }

    static class RowHeaderViewHolder extends AbstractViewHolder {
        TextView text;

        RowHeaderViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
        }
    }

    @Override
    public AbstractViewHolder onCreateCellViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_table_cell, parent, false);
        return new CellViewHolder(itemView);
    }

    @Override
    public void onBindCellViewHolder(AbstractViewHolder holder, Object cellItemModel, int columnPosition, int rowPosition) {
        CellViewHolder cellHolder = (CellViewHolder) holder;
        if (cellItemModel == null) {
            cellHolder.text.setTypeface(null, Typeface.ITALIC);
            cellHolder.text.setTextColor(ContextCompat.getColor(context, R.color.label));
            cellHolder.text.setText("NULL");
        } else {
            cellHolder.text.setTypeface(null, Typeface.NORMAL);
            cellHolder.text.setTextColor(Color.BLACK);
            cellHolder.text.setText(cellItemModel.toString());
        }
    }

    @Override
    public AbstractViewHolder onCreateColumnHeaderViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_table_header_column, parent, false);
        return new ColumnHeaderViewHolder(itemView);
    }

    @Override
    public void onBindColumnHeaderViewHolder(AbstractViewHolder holder, Object columnHeaderItemModel, int columnPosition) {
        ((ColumnHeaderViewHolder) holder).text.setText(columnHeaderItemModel.toString());
    }

    @Override
    public AbstractViewHolder onCreateRowHeaderViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_table_header_row, parent, false);
        return new RowHeaderViewHolder(itemView);
    }

    @Override
    public void onBindRowHeaderViewHolder(AbstractViewHolder holder, Object rowHeaderItemModel, int rowPosition) {
        ((RowHeaderViewHolder) holder).text.setText(rowHeaderItemModel.toString());
    }

    @Override
    public View onCreateCornerView() {
        TextView text = new TextView(context);
        text.setText("列\\行");
        text.setGravity(Gravity.CENTER);
        text.setTextSize(14);
        text.setBackgroundResource(R.drawable.table_corner_bg);
        text.setTextColor(Color.BLACK);
        TextPaint paint = text.getPaint();
        paint.setFlags(paint.getFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
        return text;
    }
}
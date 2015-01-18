package com.bltucker.utahpublicnotices;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Map;
import java.util.Set;

public final class CursorValidator {

    public boolean validateCursor(Cursor cursor, ContentValues values){

        Set<Map.Entry<String, Object>> valueSet = values.valueSet();
        for(Map.Entry<String, Object> entry : valueSet){
            String columnName = entry.getKey();
            int index = cursor.getColumnIndex(columnName);

            if(index == -1){
                return false;
            }

            String expectedValue = entry.getValue().toString();

            if(!expectedValue.equals(cursor.getString(index))){
                return false;
            }
        }

        return true;
    }
}

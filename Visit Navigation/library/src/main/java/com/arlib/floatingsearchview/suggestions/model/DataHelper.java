package com.arlib.floatingsearchview.suggestions.model;


import android.content.Context;
import android.widget.Filter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DataHelper {

        private static final String COLORS_FILE_NAME = "colors.json";

        private static List<LocationWrapper> sLocationWrappers = new ArrayList<>();

        public interface OnFindResultsListener{

                void onResults(List<LocationSuggestion> results);
        }

        public static void find(String context, String query, final OnFindResultsListener listener){

                initLocationWrapperList(context);

                new Filter(){

                        @Override
                        protected FilterResults performFiltering(CharSequence constraint) {


                                List<LocationSuggestion> suggestionList = new ArrayList<>();

                                if (!(constraint == null || constraint.length() == 0)) {

                                        for(LocationWrapper location: sLocationWrappers){

                                                if(location.getID().toUpperCase().contains(constraint.toString().toUpperCase()))
                                                        suggestionList.add(new LocationSuggestion(location));
                                        }

                                }

                                FilterResults results = new FilterResults();
                                results.values = suggestionList;
                                results.count = suggestionList.size();

                                return results;
                        }

                        @Override
                        protected void publishResults(CharSequence constraint, FilterResults results) {

                                if(listener!=null)
                                        listener.onResults((List<LocationSuggestion>)results.values);
                        }
                }.filter(query);

        }

        private static void initLocationWrapperList(String jsonString){

                if(sLocationWrappers.isEmpty()) {
                        sLocationWrappers = deserializeLocations(jsonString);
                }
        }

        private static String loadJson(Context context) {

                String jsonString;

                try {
                        InputStream is = context.getAssets().open(COLORS_FILE_NAME);
                        int size = is.available();
                        byte[] buffer = new byte[size];
                        is.read(buffer);
                        is.close();
                        jsonString = new String(buffer, "UTF-8");
                } catch (IOException ex) {
                        ex.printStackTrace();
                        return null;
                }

                return jsonString;
        }

        public static List<LocationWrapper> deserializeLocations(String jsonString){

                Gson gson = new Gson();

                Type collectionType = new TypeToken<List<LocationWrapper>>() {}.getType();
                return gson.fromJson(jsonString, collectionType);
        }

}
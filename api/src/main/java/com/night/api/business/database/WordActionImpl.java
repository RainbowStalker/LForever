package com.night.api.business.database;

import android.content.Context;

import com.night.api.base.BaseSQLiteActionImpl;
import com.night.model.entity.WordEntity;
import com.night.model.wrapper.WordTranslationWrapper;
import com.night.model.wrapper.WordWrapper;

import java.util.ArrayList;
import java.util.List;

public class WordActionImpl extends BaseSQLiteActionImpl implements WordAction {
    private static final String TABLE_NAME                       = "word";

    private static final String FIELD_WORD_NAME                  = "word_name";

    private static final String FIELD_WORD_PH_EN                 = "word_ph_en";

    private static final String FIELD_WORD_PH_EN_MP3             = "word_ph_en_mp3";

    private static final String FIELD_WORD_PH_AM                 = "word_ph_am";

    private static final String FIELD_WORD_PH_AM_MP3             = "word_ph_am_mp3";

    private static final String FIELD_WORD_PARTS                 = "word_parts";

    private static final String FIELD_WORD_MEANS                 = "word_means";

    private static final String INSERT_INTO_WORD                 = "insert into " + TABLE_NAME
            + " values(?,?,?,?,?,?,?)";

    private static final String QUERY_WORD_NAME_BY_WORD_NAME = "select " + FIELD_WORD_NAME + " from " + TABLE_NAME
            + " where " + FIELD_WORD_NAME + "=?";

    private static final String QUERY_WORD_BY_WORD_NAME="select * from "+TABLE_NAME+" where "+FIELD_WORD_NAME+"=?";

    public WordActionImpl(Context context) {
        super(context);
    }

    @Override
    public void insertIntoWord(List<WordEntity> wordEntityList) {
        database = openHelper.getWritableDatabase();
        database.beginTransaction();
        for (int i = 0; i < wordEntityList.size(); i++) {
            WordEntity wordEntity = wordEntityList.get(i);
            String wordName = wordEntity.getWord_name();
            String wordPhEn = wordEntity.getSymbols().get(0).getPh_en();
            String wordPhEnMp3 = wordEntity.getSymbols().get(0).getPh_en_mp3();
            String wordPhAm = wordEntity.getSymbols().get(0).getPh_am();
            String wordPhAmMp3 = wordEntity.getSymbols().get(0).getPh_am_mp3();

            String wordParts = "";
            String wordMeans = "";
            List<WordEntity.SymbolsBean.PartsBean> wordTranslationList = wordEntity.getSymbols().get(0).getParts();
            for (int t = 0; t < wordTranslationList.size(); t++) {
                wordParts += wordTranslationList.get(t).getPart();
                List<String> meansList = wordTranslationList.get(t).getMeans();
                for (int q = 0; q < meansList.size(); q++) {
                    wordMeans += (meansList.get(q) + "；");
                }
                wordParts += "#";
                wordMeans += "#";
            }
//            LogUtil.d(wordName);
//            LogUtil.d(wordPhEn);
//            LogUtil.d(wordPhAm);
//            LogUtil.d(wordPhAmMp3);
//            LogUtil.d(wordParts);
//            LogUtil.d(wordMeans);
            database.execSQL(INSERT_INTO_WORD,
                    new Object[] { wordName, wordPhEn, wordPhEnMp3, wordPhAm, wordPhAmMp3, wordParts, wordMeans });
        }
        close();
    }

    @Override
    public boolean isContainedInTable(String wordName) {
        database = openHelper.getReadableDatabase();
        database.beginTransaction();
        cursor = database.rawQuery(String.format(QUERY_WORD_NAME_BY_WORD_NAME, wordName), null);
        if (cursor.moveToNext()) {
            close();
            return true;
        }
        close();
        return false;
    }

    @Override
    public List<Boolean> isContainedInTable(List<String> wordNameList) {
        List<Boolean> booleanList = new ArrayList<>();
        database = openHelper.getReadableDatabase();
        database.beginTransaction();
        for (int i = 0; i < wordNameList.size(); i++) {
            cursor = database.rawQuery(QUERY_WORD_NAME_BY_WORD_NAME, new String[] { wordNameList.get(i) });
            booleanList.add(false);
        }
        while (cursor.moveToNext()) {
            String wordName = cursor.getString(getFieldIndex(FIELD_WORD_NAME));
            if (wordNameList.contains(wordName)) {
                booleanList.set(wordNameList.indexOf(wordName), true);
            }
        }
        close();
        return booleanList;
    }

    @Override
    public List<WordWrapper> getWord(List<String> wordNameList) {
        database=openHelper.getReadableDatabase();
        database.beginTransaction();

        for(int i=0;i<wordNameList.size();i++){
            cursor=database.rawQuery(QUERY_WORD_BY_WORD_NAME,new String[]{wordNameList.get(i)});
        }
        List<WordWrapper> wordWrapperList = new ArrayList<>();
        while(cursor.moveToNext()){
            String wordName=cursor.getString(getFieldIndex(FIELD_WORD_NAME));
            String wordPhEn=cursor.getString(getFieldIndex(FIELD_WORD_PH_EN));
            String wordPhEnMp3=cursor.getString(getFieldIndex(FIELD_WORD_PH_EN_MP3));
            String wordPhAm=cursor.getString(getFieldIndex(FIELD_WORD_PH_AM));
            String wordPhAmMp3=cursor.getString(getFieldIndex(FIELD_WORD_PH_AM_MP3));
            String[] wordPartArr=cursor.getString(getFieldIndex(FIELD_WORD_PARTS)).split("#");
            String[] wordMeanArr=cursor.getString(getFieldIndex(FIELD_WORD_MEANS)).split("#");

            //将原始数据转换为wrapper
            List<WordTranslationWrapper> wordTranslationWrapperList = new ArrayList<>();
            for(int i=0;i<wordPartArr.length;i++){
                WordTranslationWrapper wordTranslationWrapper = new WordTranslationWrapper(wordPartArr[i],wordMeanArr[i]);
                wordTranslationWrapperList.add(wordTranslationWrapper);
            }
            wordWrapperList.add(new WordWrapper(wordName,wordPhEn,wordPhEnMp3,wordPhAm,wordPhAmMp3,wordTranslationWrapperList));
        }
        close();
        return wordWrapperList;
    }

}
package io.square1.laravelConnect.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

public class Filter implements Parcelable {



    private HashMap<String, ArrayList<Criteria>> mCriteria;

    public Filter(){
        mCriteria = new HashMap<>();
    }

    public Filter addCriteria(Criteria criteria){
        ArrayList<Criteria> criteriaArrayList = getCriteriaForProperty(criteria.getPropertyName());
        criteriaArrayList.add(criteria);
        return this;
    }

    private ArrayList<Criteria> getCriteriaForProperty(String propertyName){

        ArrayList<Criteria> criteriaArrayList = mCriteria.get(propertyName);
        if(criteriaArrayList == null){
            criteriaArrayList = new ArrayList<>();
            mCriteria.put(propertyName, criteriaArrayList);
        }

        return criteriaArrayList;

    }

    public ArrayList<String> getNames(){
        return new ArrayList<>(mCriteria.keySet());
    }

    public ArrayList<Criteria> getCriteria(String paramName){
        return mCriteria.get(paramName);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.mCriteria);
    }

    protected Filter(Parcel in) {
        this.mCriteria = (HashMap<String, ArrayList<Criteria>>) in.readSerializable();
    }

    public static final Parcelable.Creator<Filter> CREATOR = new Parcelable.Creator<Filter>() {
        @Override
        public Filter createFromParcel(Parcel source) {
            return new Filter(source);
        }

        @Override
        public Filter[] newArray(int size) {
            return new Filter[size];
        }
    };
}

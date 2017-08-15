package io.square1.laravelConnect.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by roberto on 25/07/2017.
 */

public class Criteria<T> implements Parcelable {

   enum Verb{
        Equal,
        NotEqual,
        GreaterThan,
        GreaterThanOrEqual,
        LowerThan,
        LowerThanOrEqual,
        Contains
   }

    private String mPropertyName;
    private String mPropertyValue;

    private Verb mCriteria;

    private Criteria(ModelProperty<T> property, Verb criteria){
        mPropertyName = property.getName();
        mPropertyValue = String.valueOf(property.getValue());
        mCriteria = criteria;
    }

    public String getPropertyName(){
        return mPropertyName;
    }

    public String getPropertyValue(){
        return mPropertyValue;
    }

    public String getVerb(){
        return String.valueOf(mCriteria);
    }

    public static Criteria equalsTo(ModelProperty property){
        return new Criteria(property, Verb.Equal);
    }

    public static Criteria notEqualsTo(ModelProperty property){
        return new Criteria(property, Verb.NotEqual);
    }

    public static Criteria contains(ModelProperty property){
        return new Criteria(property, Verb.Contains);
    }

    public static Criteria greaterThan(ModelProperty property, boolean includeEqual){
        return new Criteria(property, includeEqual ? Verb.GreaterThanOrEqual : Verb.GreaterThan);
    }

    public static Criteria lowerThan(ModelProperty property, boolean includeEqual){
        return new Criteria(property, includeEqual ? Verb.LowerThanOrEqual : Verb.LowerThan);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mPropertyName);
        dest.writeString(this.mPropertyValue);
        dest.writeInt(this.mCriteria == null ? -1 : this.mCriteria.ordinal());
    }

    protected Criteria(Parcel in) {
        this.mPropertyName = in.readString();
        this.mPropertyValue = in.readString();
        int tmpMCriteria = in.readInt();
        this.mCriteria = tmpMCriteria == -1 ? null : Verb.values()[tmpMCriteria];
    }

    public static final Parcelable.Creator<Criteria> CREATOR = new Parcelable.Creator<Criteria>() {
        @Override
        public Criteria createFromParcel(Parcel source) {
            return new Criteria(source);
        }

        @Override
        public Criteria[] newArray(int size) {
            return new Criteria[size];
        }
    };
}

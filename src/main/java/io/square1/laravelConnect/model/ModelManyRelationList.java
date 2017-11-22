package io.square1.laravelConnect.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import io.square1.laravelConnect.client.LaravelConnectClient;
import io.square1.laravelConnect.client.ApiRequest;
import io.square1.laravelConnect.client.Sort;
import io.square1.laravelConnect.client.results.Pagination;
import io.square1.laravelConnect.client.results.Result;
import io.square1.laravelConnect.client.results.ResultList;

/**
 * Created by roberto on 03/06/2017.
 */

public class ModelManyRelationList<T extends BaseModel>  implements List<T>, LaravelConnectClient.Observer, Parcelable {

    public static class Builder {

        private Class mParentClass;
        private Integer mParentId;
        private Class mRelationClass;
        private String mRelationName;
        private Filter mFilter;
        private Sort mSort;
        private Integer mPageSize;

        public Builder(){

        }

        public Builder relation(ModelManyRelation relation){


            return parentClass(relation.getParent().getClass())
                    .parentId(relation.getParent().getIdValue())
                    .relationClass(relation.getDataClass())
                    .relationName(relation.getName());
        }

        Builder parentClass(Class parentClass){
            mParentClass = parentClass;
            return this;
        }

        Builder parentId(Integer parentId){
            mParentId = parentId;
            return this;
        }

        Builder relationClass(Class relationClass){
            mRelationClass = relationClass;
            return this;
        }

        Builder relationName(String relationName){
            mRelationName = relationName;
            return this;
        }

        public Builder filter(Filter filter){
            mFilter = filter;
            return this;
        }

        public Builder sort(Sort sort){
            mSort = sort;
            return this;
        }

        public Builder pageSize(Integer pageSize){
            mPageSize = pageSize;
            return this;
        }

        public ModelManyRelationList build(){
            return new ModelManyRelationList(mParentClass, mParentId, mRelationClass,
                    mRelationName, mFilter, mSort, mPageSize);
        }

    }

    private LaravelConnectClient.Observer mObserver;
    private Pagination mCurrentPage;
    private ArrayList<T> mStorage;

    private Class mParentClass;
    private Integer mParentId;
    private Class mRelationClass;
    private String mRelationName;
    private Filter mFilter;
    private Sort mSort;
    private Integer mPageSize;

    ModelManyRelationList(ModelManyRelation relation){
       this(relation, null, null);
    }

    ModelManyRelationList(ModelManyRelation relation, Filter filter, Sort sort){
        this(relation.getParent().getClass(), relation.getParent().getIdValue(),
                relation.getDataClass(), relation.getName(), filter, sort,
                Pagination.DEFAULT_PAGE_SIZE);
    }

    ModelManyRelationList(Class parentClass, Integer parentId, Class relationClass,
                          String relationName, Filter filter, Sort sort, Integer pageSize){
        mParentClass = parentClass;
        mParentId = parentId;
        mRelationClass = relationClass;
        mRelationName = relationName;
        mCurrentPage = Pagination.NOPAGE;
        mPageSize = pageSize;
        mStorage = new ArrayList<>();
        mFilter = filter;
        mSort = sort;
    }

    public Builder buildUpon(){
        return new Builder()
                .filter(mFilter)
                .sort(mSort)
                .parentClass(mParentClass)
                .parentId(mParentId)
                .relationClass(mRelationClass)
                .relationName(mRelationName);
    }



    public ApiRequest next(LaravelConnectClient.Observer observer){
        if(mCurrentPage.hasNext() == true) {
           return index(mCurrentPage.next(), mPageSize, observer);
        }

        return null;
    }

    private ApiRequest index(int page, int perPage, LaravelConnectClient.Observer observer){

        mObserver = observer;

        LaravelConnectClient apiClient = LaravelConnectClient.getInstance();
        return apiClient.list(mParentClass, mParentId, mRelationClass, mRelationName, page, perPage, this, mFilter, mSort);
     }



    @Override
    public int size() {
        return mStorage.size();
    }

    @Override
    public boolean isEmpty() {
        return mStorage.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return mStorage.contains(o);
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return mStorage.iterator();
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return mStorage.toArray();
    }

    @NonNull
    @Override
    public <T1> T1[] toArray(@NonNull T1[] t1s) {
        return mStorage.toArray(t1s);
    }

    @Override
    public boolean add(T t) {
        return mStorage.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return mStorage.remove(o);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> collection) {
        return mStorage.containsAll(collection);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends T> collection) {
        return mStorage.addAll(collection);
    }

    @Override
    public boolean addAll(int i, @NonNull Collection<? extends T> collection) {
        return mStorage.addAll(i, collection);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> collection) {
        return mStorage.removeAll(collection);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> collection) {
        return mStorage.retainAll(collection);
    }

    @Override
    public void clear() {
        mStorage.clear();
    }

    @Override
    public T get(int i) {
        return mStorage.get(i);
    }

    @Override
    public T set(int i, T t) {
        return mStorage.set(i, t);
    }

    @Override
    public void add(int i, T t) {
        mStorage.add(i, t);
    }

    @Override
    public T remove(int i) {
        return mStorage.remove(i);
    }

    @Override
    public int indexOf(Object o) {
        return mStorage.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return mStorage.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return mStorage.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator(int i) {
        return mStorage.listIterator(i);
    }

    @NonNull
    @Override
    public List<T> subList(int i, int i1) {
        return mStorage.subList(i, i1);
    }

    @Override
    public void onRequestCompleted(Result result) {

        if(result.isSuccessful() == true) {
            if (result instanceof ResultList) {
                mCurrentPage = ((ResultList)result).getPagination();
                if (mCurrentPage.isFirstPage() == true) {
                    clear();
                }
                addAll(((ResultList)result).getData());
            }

            if (mObserver != null) {
                mObserver.onRequestCompleted(result);
            }
        }
    }

    @Override
    public String toString() {
        String path = ModelUtils.pathForModel(mParentClass);
        return  path  + "/" + mParentId + "/" + mRelationName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.mParentClass);
        dest.writeValue(this.mParentId);
        dest.writeSerializable(this.mRelationClass);
        dest.writeString(this.mRelationName);
        dest.writeParcelable(this.mFilter, flags);
        dest.writeParcelable(this.mSort, flags);
    }

    protected ModelManyRelationList(Parcel in) {
        this.mParentClass = (Class) in.readSerializable();
        this.mParentId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mRelationClass = (Class) in.readSerializable();
        this.mRelationName = in.readString();
        this.mFilter = in.readParcelable(Filter.class.getClassLoader());
        this.mSort = in.readParcelable(Sort.class.getClassLoader());
    }

    public static final Parcelable.Creator<ModelManyRelationList> CREATOR = new Parcelable.Creator<ModelManyRelationList>() {
        @Override
        public ModelManyRelationList createFromParcel(Parcel source) {
            return new ModelManyRelationList(source);
        }

        @Override
        public ModelManyRelationList[] newArray(int size) {
            return new ModelManyRelationList[size];
        }
    };
}

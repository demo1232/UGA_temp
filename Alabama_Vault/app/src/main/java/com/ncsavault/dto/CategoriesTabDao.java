package com.ncsavault.dto;

/**
 *Class will hold the data for for categories tab.
 */

public class CategoriesTabDao {

    //this is category id
    private long CategoriesId;

    //this is category name
    private String CategoriesName;

    //this is category url
    private String CategoriesUrl;

    //this is category keyword
    private String CategoriesKeyword;

    //this is category position
    private long index_position;

    //this is category modified
    private long categories_modified;

    /**
     * @return Gets the value of CategoriesId and returns CategoriesId
     */
    public long getCategoriesId() {
        return CategoriesId;
    }

    /**
     * Sets the CategoriesId
     * You can use getCategoriesId() to get the value of CategoriesId
     */
    public void setCategoriesId(long categoriesId) {
        CategoriesId = categoriesId;
    }

    /**
     * @return Gets the value of CategoriesName and returns CategoriesName
     */
    public String getCategoriesName() {
        return CategoriesName;
    }

    /**
     * Sets the CategoriesName
     * You can use getCategoriesName() to get the value of CategoriesName
     */
    public void setCategoriesName(String categoriesName) {
        CategoriesName = categoriesName;
    }

    /**
     * @return Gets the value of CategoriesUrl and returns CategoriesUrl
     */
    public String getCategoriesUrl() {
        return CategoriesUrl;
    }

    /**
     * Sets the CategoriesUrl
     * You can use getCategoriesUrl() to get the value of CategoriesUrl
     */
    public void setCategoriesUrl(String categoriesUrl) {
        CategoriesUrl = categoriesUrl;
    }

    /**
     * @return Gets the value of CategoriesKeyword and returns CategoriesKeyword
     */
    public String getCategoriesKeyword() {
        return CategoriesKeyword;
    }

    /**
     * Sets the CategoriesKeyword
     * You can use getCategoriesKeyword() to get the value of CategoriesKeyword
     */
    public void setCategoriesKeyword(String categoriesKeyword) {
        CategoriesKeyword = categoriesKeyword;
    }

    /**
     * @return Gets the value of index_position and returns index_position
     */
    public long getIndex_position() {
        return index_position;
    }

    /**
     * Sets the index_position
     * You can use getIndex_position() to get the value of index_position
     */
    public void setIndex_position(long index_position) {
        this.index_position = index_position;
    }

    /**
     * @return Gets the value of categories_modified and returns categories_modified
     */
    public long getCategories_modified() {
        return categories_modified;
    }

    /**
     * Sets the categories_modified
     * You can use getCategories_modified() to get the value of categories_modified
     */
    public void setCategories_modified(long categories_modified) {
        this.categories_modified = categories_modified;
    }
}

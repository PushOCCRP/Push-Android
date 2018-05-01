package com.pushapp.press.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Bryan Lamtoo.
 */
public class ArticlePost implements Serializable {

        @SerializedName("start_date")
        @Expose
        private Integer startDate;
        @SerializedName("end_date")
        @Expose
        private Integer endDate;
        @SerializedName("total_results")
        @Expose
        private Integer totalResults = 0;
        @SerializedName("total_pages")
        @Expose
        private Integer totalPages;
        @Expose
        private Integer page;
        @Expose
        private Object results = new Object();

        @Expose
        private ArrayList<String> categories;

        /**
         *
         * @return
         * The startDate
         */
        public Integer getStartDate() {
            return startDate;
        }

        /**
         *
         * @param startDate
         * The start_date
         */
        public void setStartDate(Integer startDate) {
            this.startDate = startDate;
        }

        /**
         *
         * @return
         * The endDate
         */
        public Integer getEndDate() {
            return endDate;
        }

        /**
         *
         * @param endDate
         * The end_date
         */
        public void setEndDate(Integer endDate) {
            this.endDate = endDate;
        }

        /**
         *
         * @return
         * The totalResults
         */
        public Integer getTotalResults() {
            return totalResults;
        }

        /**
         *
         * @param totalResults
         * The total_results
         */
        public void setTotalResults(Integer totalResults) {
            this.totalResults = totalResults;
        }

        /**
         *
         * @return
         * The totalPages
         */
        public Integer getTotalPages() {
            return totalPages;
        }

        /**
         *
         * @param totalPages
         * The total_pages
         */
        public void setTotalPages(Integer totalPages) {
            this.totalPages = totalPages;
        }

        /**
         *
         * @return
         * The page
         */
        public Integer getPage() {
            return page;
        }

        /**
         *
         * @param page
         * The page
         */
        public void setPage(Integer page) {
            this.page = page;
        }

        /**
         *
         * @return
         * The results
         */
        public Object getResults(){
            return results;
        }

        /**
         *
         * @param results
         * The results
         */
        public void setResults(Object results) {
            this.results = results;
        }

        /**
         *
         * @return
         * The categories
         */
        public ArrayList<String> getCategories(){
            return categories;
        }

        /**
         *
         * @param categories
         * The results
         */
        public void setCategories(ArrayList<String> categories) {
            this.categories = categories;
        }

}
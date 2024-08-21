package com.pherom.easysaleassignment.network;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class UserPageResponse {

    private int page;
    @SerializedName("per_page")
    private int perPage;
    private int total;
    @SerializedName("total_pages")
    private int totalPages;
    List<UserDto> data;

    public int getPage() {
        return page;
    }

    public int getPerPage() {
        return perPage;
    }

    public int getTotal() {
        return total;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public List<UserDto> getData() {
        return data;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void setData(List<UserDto> data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPageResponse that = (UserPageResponse) o;
        return page == that.page;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(page);
    }
}

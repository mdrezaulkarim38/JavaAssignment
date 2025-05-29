package com.system.library.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryId;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "category")
    private List<Book> books;

    public String getCategoryName(){
        return this.name;
    }
    public void setCategoryName(String categoryName){
        this.name = categoryName;
    }
    public Integer getCategoryId(){
        return this.categoryId;
    }
    public void setCategoryId(Integer categoryId){
        this.categoryId = categoryId;
    }


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

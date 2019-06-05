package com.wanggang.familytree.widget;

import android.view.View;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by wg on 2017/4/24.
 * 父亲孩子表示法
 */

public class PersonEntity extends PersonData {

    // 父亲节点信息
    public PersonEntity father;
    // 子节点信息
    public List<PersonEntity> childs;

    public View.OnClickListener clickListener;

    public PersonEntity(String name, int gender, long id, int level, String avatar, String relationship) {
        super(name, gender, id, level, avatar, relationship);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}

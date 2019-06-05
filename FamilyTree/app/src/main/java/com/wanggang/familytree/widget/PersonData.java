package com.wanggang.familytree.widget;

/**
 * Created by wg on 2017/4/20.
 */

public class PersonData {
    /**
     * name : 王明
     * gender : 1
     * id : 345
     * level : 54
     * avatar : http://www.qqpk.cn/Article/UploadFiles/201202/20120219130832435.jpg
     * relationship : 我
     */

    public long id;
    public long fid;
    public int level;
    public String name;
    public String avatar;
    public String rank;
    public String children_gender;
    public int gender;

    public String relationship;

    public long fromId; // 本节点的来源id
    Ponit centerPoint; // 显示位置的中心点坐标

    public PersonData() {
        this.centerPoint = new Ponit(0, 0);
    }

    public PersonData(String name, int gender, long id, int level, String avatar, String relationship) {
        this.name = name;
        this.gender = gender;
        this.id = id;
        this.level = level;
        this.avatar = avatar;
        this.relationship = relationship;

        this.centerPoint = new Ponit(0, 0);
    }

    public Ponit getCenterPoint() {
        return centerPoint;
    }

    public void setCenterPoint(int centerX, int centerY) {

        if (this.centerPoint == null) {
            this.centerPoint = new Ponit(0, 0);
        }

        this.centerPoint.coordinateX = centerX;
        this.centerPoint.coordinateY = centerY;
    }
}

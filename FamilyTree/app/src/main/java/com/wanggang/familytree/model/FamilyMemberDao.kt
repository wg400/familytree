package com.wanggang.familytree.model

import android.arch.persistence.room.*


@Dao
interface BaseDao<T> {

    @Insert
    fun insertItem(item: T) //插入单条数据

    @Insert
    fun insertItems(items: List<T>) //插入list数据

    @Delete
    fun deleteItem(item: T) //删除item

    @Update
    fun updateItem(item: T) //更新item

}

@Dao
interface FamilyMemberDao: BaseDao<FamilyMemberEntity> {

    /**
     * 根据id查询FamilyMemberEntity
     */
    @Query("SELECT * FROM members WHERE id = :id ")
    fun getMemberById(id: Long): FamilyMemberEntity

    /**
     * 根据id查询配偶信息
     */
    @Query("SELECT * FROM members WHERE spouseId = :spouseId ")
    fun getSpouseMemberById(spouseId: Long): FamilyMemberEntity

    /**
     * 根据id查询所有子FamilyMemberEntity集合
     */
    @Query("SELECT * FROM members WHERE fatherId = :id ")
    fun getChildMembers(id: Long): List<FamilyMemberEntity>

    /**
     * 查询全部结果
     */
    @Query("SELECT * FROM members")
    fun getAllMembers(): List<FamilyMemberEntity>

}
package com.wanggang.familytree.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.content.Context
import com.google.gson.annotations.SerializedName
import com.wanggang.familytree.widget.PersonEntity

@Entity(tableName = "members")
data class FamilyMemberEntity(var name: String) {

    constructor() : this("")

    // 主键自增
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @SerializedName("id")
    var id: Long = 0

    // 父亲id
    @ColumnInfo(name = "fatherId")
    @SerializedName("fatherId")
    var fatherId: Long? = null

    // 配偶id
    @ColumnInfo(name = "spouseId")
    @SerializedName("spouseId")
    var spouseId: Long? = null

    // 外键
    @ColumnInfo(name = "sex")
    @SerializedName("sex")
    var sex: Int = 0

    // 头像路径
    @ColumnInfo(name = "phone")
    @SerializedName("phone")
    var phone: String? = null // 手机号

    // 头像路径
    @ColumnInfo(name = "imagePath")
    @SerializedName("imagePath")
    var imagePath: String? = null

    override fun toString(): String {
        return "{name='$name', id=$id, sex=$sex}"
    }

    fun genFamilyMember(context: Context, level: Int): PersonEntity {
        var personEntity = PersonEntity(name, sex, id, level, imagePath, null)

        personEntity.childs = getChildMembers1(context, level + 1)

        return personEntity
    }

    fun getChildMembers1(context: Context, level: Int): List<PersonEntity> {
        var memberList = FamilyDataBaseHelper.getInstance(context).getChildMembers(id)
        var personEntityList = ArrayList<PersonEntity>()
        for (member in memberList) {
            personEntityList.add(member.genFamilyMember(context, level))
        }
        return personEntityList
    }

    /**
     * 将数据库数据转换成家族树成员模型
     * */
    fun generateMember(context: Context, level: Int) : FamilyMemberModel {

        var familyMemberModel = FamilyMemberModel(this)

        familyMemberModel.level = level

        familyMemberModel.spouseEntity = FamilyDataBaseHelper.getInstance(context).getSpouseMember(id)

        familyMemberModel.childModels = getChildMembers(context, level + 1)

        return familyMemberModel
    }

    private fun getChildMembers(context: Context, level: Int): List<FamilyMemberModel> {
        var memberList = FamilyDataBaseHelper.getInstance(context).getChildMembers(id)
        var memberModelList = ArrayList<FamilyMemberModel>()
        for (member in memberList) {
            memberModelList.add(member.generateMember(context, level))
        }
        return memberModelList
    }

}
package com.wanggang.familytree

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.wanggang.familytree.familytree.FamilyTreeAdapter
import com.wanggang.familytree.model.FamilyDataBaseHelper
import com.wanggang.familytree.model.FamilyMemberEntity
import com.wanggang.familytree.model.FamilyMemberModel
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main3.*

class Main3Activity : AppCompatActivity() {

    val TAG = "FamilyMemberEntity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        initView()

    }

    private fun initView() {
        familyMemberLayout.familyTreeAdapter = FamilyTreeAdapter()

        initData()

    }

    private fun initData() {

        Flowable.create(FlowableOnSubscribe<FamilyMemberModel> {

            // sharePreference存储firstIn，标记当前是否第一次启动
            var firstIn by Preference<String>("firstIn", "1")
            println("firstIn = $firstIn")
            if ("1" == firstIn) {

                // 应用第一次启动，则往数据库插入一些数据

                // 插入第一条数据，同时也是整个家族树的跟节点
                var familyMember = FamilyMemberEntity("王根")
                familyMember.imagePath = "111.jpg"
                familyMember.phone = "18156094171"
                familyMember.sex = 1
                FamilyDataBaseHelper.getInstance(this).insertMember(familyMember)

                familyMember = FamilyMemberEntity("王明")
                familyMember.imagePath = "222.jpg"
                familyMember.phone = "18156096666"
                familyMember.fatherId = 1 // 父亲id为1，表示其父亲未第1条插入的数据，也就是上面的王根
                familyMember.sex = 1
                FamilyDataBaseHelper.getInstance(this).insertMember(familyMember)

                familyMember = FamilyMemberEntity("王芸")
                familyMember.imagePath = "222.jpg"
                familyMember.phone = "18156096666"
                familyMember.fatherId = 1 // 父亲id为1，表示其父亲未第1条插入的数据，也就是上面的王根
                familyMember.sex = 0
                FamilyDataBaseHelper.getInstance(this).insertMember(familyMember)

                familyMember = FamilyMemberEntity("王恩")
                familyMember.imagePath = "222.jpg"
                familyMember.phone = "18156096666"
                familyMember.fatherId = 2 // 父亲id为2，表示其父亲未第2条插入的数据，也就是上面的王明
                familyMember.sex = 0
                FamilyDataBaseHelper.getInstance(this).insertMember(familyMember)

                firstIn = "0"
            }

            var familyMember = FamilyDataBaseHelper.getInstance(this).getFamilyMember(1)

            if (familyMember != null) {
                var familyMemberModel = familyMember.generateMember(this, 0)
                it.onNext(familyMemberModel)
            } else {
                it.onError(Throwable("空的"))
            }
            it.onComplete()

        }, BackpressureStrategy.ERROR)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                familyMemberLayout.familyTreeAdapter!!.dealWithData(it) {
                    familyMemberLayout.displayUI()
                }
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == Activity.RESULT_OK) {
            initData()
        }
    }
}

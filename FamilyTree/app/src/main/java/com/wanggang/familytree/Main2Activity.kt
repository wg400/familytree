package com.wanggang.familytree

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.wanggang.familytree.model.FamilyDataBaseHelper
import com.wanggang.familytree.model.FamilyMemberEntity
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main2.*


class Main2Activity : AppCompatActivity() {

    var imagePath: String? = ""
    var mFamilyMember: FamilyMemberEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        var id = intent.getLongExtra("id", -1L)
        var fatherId = intent.getLongExtra("fatherId", -1L)
        var spouseId = intent.getLongExtra("spouseId", -1L)

        if (id != -1L) {
            Single.create(SingleOnSubscribe<FamilyMemberEntity> {
                var familyMember = FamilyDataBaseHelper.getInstance(this).getFamilyMember(id)
                it.onSuccess(familyMember)
            }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mFamilyMember = it
                    tvName.setText(it.name)
                    tvPhone.setText(it.phone)

                    var options = RequestOptions()
                        .placeholder(R.drawable.img_head_default_woman)
                        .error(R.drawable.img_head_default_woman)
                        .override(400,400)
                    Glide.with(this).load(it.imagePath).apply(options).into(ivHead)
                    imagePath = it.imagePath
                    if (it.sex == 1) {
                        spinnerSex.setSelection(2)
                    } else {
                        spinnerSex.setSelection(1)
                    }
                }, {

                })
        }

        ivHead.setOnClickListener {
            PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .enableCrop(true)// 是否裁剪 true or false
                .compress(true)// 是否压缩 true or false
                .withAspectRatio(1, 1)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .cropWH(200, 200)// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
                .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                .rotateEnabled(false) // 裁剪是否可旋转图片 true or false
                .compressSavePath("familyTree/")//压缩图片保存地址
                .forResult(PictureConfig.CHOOSE_REQUEST)
        }

        button.setOnClickListener {
            Flowable.create(FlowableOnSubscribe<FamilyMemberEntity> {

                if (mFamilyMember == null) {
                    mFamilyMember = FamilyMemberEntity(tvName.text.toString())
                }
                mFamilyMember!!.name = tvName.text.toString()
                mFamilyMember!!.phone = tvPhone.text.toString()
                mFamilyMember!!.imagePath = imagePath

                mFamilyMember!!.sex = if (spinnerSex.selectedItemId == 1L) 0 else 1

                when {
                    id != -1L -> {
                        mFamilyMember!!.id = id
                        FamilyDataBaseHelper.getInstance(this).updateMember(mFamilyMember!!)
                    }
                    fatherId != -1L -> {
                        mFamilyMember!!.fatherId = fatherId
                        FamilyDataBaseHelper.getInstance(this).insertMember(mFamilyMember!!)
                    }
                    spouseId != -1L -> {
                        mFamilyMember!!.spouseId = spouseId
                        FamilyDataBaseHelper.getInstance(this).insertMember(mFamilyMember!!)
                    }
                }

                it.onNext(mFamilyMember)
                it.onComplete()

            }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show()

                    setResult(Activity.RESULT_OK)
                    finish()

                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    // 图片、视频、音频选择结果回调
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的

                    if (selectList.isNotEmpty()) {
                        imagePath = selectList[0].compressPath
                        println("imagepath = $imagePath")
                        var options = RequestOptions()
                            .placeholder(R.drawable.img_head_default_woman)
                            .error(R.drawable.img_head_default_woman)
                            .override(400,400)
                        Glide.with(this).load(imagePath).apply(options).into(ivHead)
                    }
                }
            }
        }
    }


}

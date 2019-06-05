package com.wanggang.familytree.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.view.View;
import com.wanggang.familytree.Main2Activity;
import com.wanggang.familytree.R;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by wg on 2017/4/20.
 */

public class FamilyTreeAdapter {

    public PersonEntity personEntity;

    public List<PersonEntity> dataList;


    public TreeMap<Integer, Point> pointMap; // 记录每一行的起始位置

    public int l, t, r, b; // 上下左右的边界
    public int rootLevel; // 跟节点的层级

    private AlertDialog mDialog;

    public void addData(PersonEntity personEntity) {
        this.personEntity = personEntity;
        dataList = new LinkedList<>();
        pointMap = new TreeMap<>();
        rootLevel = personEntity.level;

        // 初始化竖直一排的数据
        // 基本数据
        addBasePersonEntity(personEntity);
        addBaseChildEntity(personEntity);
        addBaseParentEntity(personEntity);
    }

    // 基础数据添加
    public Ponit addBasePersonEntity(PersonEntity entity) {
        addToList(entity);
        entity.setCenterPoint(0, personEntity.level - entity.level);
        pointMap.put(entity.level, new Point(0,0));

        return entity.centerPoint;
    }

    public void addBaseChildEntity(PersonEntity entity) {
        PersonEntity child = getCenterChild(entity);
        if (child != null) {
            addBasePersonEntity(child);
            // 儿子保存父亲的坐标点
            child.centerPoint.connecPonit = entity.centerPoint;
            addBaseChildEntity(child);

            int childSize = entity.childs.size();
            int childIndex = childSize / 2;
            // 当前结点左边的兄弟节点
            for (int i = childIndex - 1; i >= 0; i--) {
                addLeftChild(entity, entity.childs.get(i));
            }

            // 当前结点右边的兄弟节点
            for (int i = childIndex + 1; i < childSize; i++) {
                addRightChild(entity, entity.childs.get(i));
            }
        }
    }

    public void addBaseParentEntity(PersonEntity entity) {
        PersonEntity father = entity.father;
        if (father != null) {

            addBasePersonEntity(father);

            t = father.level;

            if (father.childs != null && father.childs.size() > 0) {
                int childSize = father.childs.size();
                int childIndex = 0;
                for (int i = 0; i < childSize; i++) {
                    if (entity.id == father.childs.get(i).id) {
                        childIndex = i;
                        break;
                    }
                }

                // 当前结点左边的兄弟节点
                for (int i = childIndex - 1; i >= 0; i--) {
                    addLeftChild(father, father.childs.get(i));
                }

                // 当前结点右边的兄弟节点
                for (int i = childIndex + 1; i < childSize; i++) {
                    addRightChild(father, father.childs.get(i));
                }
            }

            entity.centerPoint.connecPonit = father.centerPoint;
            addBaseParentEntity(father);
        }
    }

    // 添加左边的孩子节点
    public void addLeftChild(PersonEntity parentEntity, PersonEntity childEntity) {
        if (childEntity.childs == null || childEntity.childs.size() == 0) {
            Point p = getLevelPonit(childEntity.level);
            p.x = p.x - 1;
            childEntity.setCenterPoint(p.x, personEntity.level - childEntity.level);
            pointMap.put(childEntity.level, p);

            if (b < childEntity.level) {
                b = childEntity.level;
            }
            if (l > p.x) {
                l = p.x;
            }
        } else {
            int childSize = childEntity.childs.size();
            for (int i = childSize - 1; i >= 0; i--) {
                addLeftChild(childEntity, childEntity.childs.get(i));
            }
            Point p = getLevelPonit(childEntity.level);
            int pointX = (childEntity.childs.get(0).centerPoint.coordinateX + childEntity.childs.get(childSize - 1).centerPoint.coordinateX) / 2;
            p.x = pointX;
            childEntity.setCenterPoint(pointX, personEntity.level - childEntity.level);
            pointMap.put(childEntity.level, p);
        }
        if (parentEntity.centerPoint == null) {
            parentEntity.centerPoint = new Ponit(0, 0);
        }
        childEntity.centerPoint.connecPonit = parentEntity.centerPoint;
        addToList(childEntity);
    }

    // 添加右边边的孩子节点
    public void addRightChild(PersonEntity parentEntity, PersonEntity childEntity) {
        if (childEntity.childs == null || childEntity.childs.size() == 0) {
            Point p = getLevelPonit(childEntity.level);
            p.y = p.y + 1;
            childEntity.setCenterPoint(p.y, personEntity.level - childEntity.level);
            pointMap.put(childEntity.level, p);

            if (b < childEntity.level) {
                b = childEntity.level;
            }
            if (r < p.y) {
                r = p.y;
            }
        } else {
            int childSize = childEntity.childs.size();
            for (int i = 0; i < childSize; i++) {
                addRightChild(childEntity, childEntity.childs.get(i));
            }
            Point p = getLevelPonit(childEntity.level);
            int pointY = (childEntity.childs.get(0).centerPoint.coordinateX + childEntity.childs.get(childSize - 1).centerPoint.coordinateX) / 2;
            p.y = pointY;
            childEntity.setCenterPoint(pointY, personEntity.level - childEntity.level);
            pointMap.put(childEntity.level, p);
        }
        if (parentEntity.centerPoint == null) {
            parentEntity.centerPoint = new Ponit(0, 0);
        }
        childEntity.centerPoint.connecPonit = parentEntity.centerPoint;
        addToList(childEntity);
    }

    public void addToList(PersonEntity entity) {
        entity.clickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                final PersonView personView = (PersonView) v;
                if (mDialog == null) {
                    mDialog = new AlertDialog.Builder(v.getContext()).setItems(R.array.dialog_menu, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(v.getContext(), Main2Activity.class);
                            switch (which) {
                                case 0:
                                    intent.putExtra("id", personView.personEntity.id);
                                    break;
                                case 1:
                                    intent.putExtra("fatherId", personView.personEntity.id);
                                    break;
                                case 2:
                                    intent.putExtra("spouseId", personView.personEntity.id);
                                    break;
                            }
                            ((Activity) v.getContext()).startActivityForResult(intent, 111);
                        }
                    }).create();
                }
                mDialog.show();

            }
        };
        dataList.add(entity);
    }

    public PersonEntity getCenterChild(PersonEntity personEntity) {
        if (personEntity.childs != null && personEntity.childs.size() > 0) {
            int size = personEntity.childs.size();
            return personEntity.childs.get(size / 2);
        }
        return null;
    }

    public PersonView getPersonView(PersonView personView, View parent, PersonEntity personEntity) {
        if (personView == null) {
            personView = PersonView.getPersonView(parent.getContext());
        }
        personView.setPersonEntity(personEntity);
        personView.setOnClickListener(personEntity.clickListener);
        return personView;
    }

    public Point getLevelPonit(int level) {
        Point p = pointMap.get(level);
        if (p == null) {
            p = new Point(0, 0);
        }

        while (true) {
            Point parentPoint = pointMap.get(--level);
            if (parentPoint == null) {
                break;
            }
            if (parentPoint.x < p.x) {
                p.x = parentPoint.x;
            }
            if (parentPoint.y > p.y) {
                p.y = parentPoint.y;
            }
        }
        return p;
    }

}

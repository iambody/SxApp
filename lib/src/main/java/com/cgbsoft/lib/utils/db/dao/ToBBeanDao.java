package com.cgbsoft.lib.utils.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.cgbsoft.lib.base.model.bean.ToBBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "TO_BBEAN".
*/
public class ToBBeanDao extends AbstractDao<ToBBean, String> {

    public static final String TABLENAME = "TO_BBEAN";

    /**
     * Properties of entity ToBBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, String.class, "id", true, "ID");
        public final static Property OrganizationName = new Property(1, String.class, "organizationName", false, "ORGANIZATION_NAME");
        public final static Property PreparedforNum = new Property(2, int.class, "preparedforNum", false, "PREPAREDFOR_NUM");
        public final static Property SubmitTime = new Property(3, String.class, "submitTime", false, "SUBMIT_TIME");
        public final static Property TeamNum = new Property(4, int.class, "teamNum", false, "TEAM_NUM");
        public final static Property OrganizationId = new Property(5, String.class, "organizationId", false, "ORGANIZATION_ID");
        public final static Property BeatRank = new Property(6, int.class, "beatRank", false, "BEAT_RANK");
        public final static Property AdviserPhoto = new Property(7, String.class, "adviserPhoto", false, "ADVISER_PHOTO");
        public final static Property AdviserStatus = new Property(8, int.class, "adviserStatus", false, "ADVISER_STATUS");
        public final static Property AdviserOrganization = new Property(9, String.class, "adviserOrganization", false, "ADVISER_ORGANIZATION");
        public final static Property AdviserName = new Property(10, String.class, "adviserName", false, "ADVISER_NAME");
        public final static Property AdviserLevel = new Property(11, int.class, "adviserLevel", false, "ADVISER_LEVEL");
        public final static Property IsEmployee = new Property(12, String.class, "isEmployee", false, "IS_EMPLOYEE");
        public final static Property AdviserNumber = new Property(13, String.class, "adviserNumber", false, "ADVISER_NUMBER");
        public final static Property CompletedOrderCount = new Property(14, int.class, "completedOrderCount", false, "COMPLETED_ORDER_COUNT");
        public final static Property RejectReason = new Property(15, String.class, "rejectReason", false, "REJECT_REASON");
        public final static Property ServiceCheckStatus = new Property(16, int.class, "serviceCheckStatus", false, "SERVICE_CHECK_STATUS");
        public final static Property AdviserPerformance = new Property(17, String.class, "adviserPerformance", false, "ADVISER_PERFORMANCE");
        public final static Property AdviserState = new Property(18, String.class, "adviserState", false, "ADVISER_STATE");
        public final static Property Category = new Property(19, String.class, "category", false, "CATEGORY");
        public final static Property AdviserRank = new Property(20, String.class, "adviserRank", false, "ADVISER_RANK");
        public final static Property MyCustomersCount = new Property(21, int.class, "myCustomersCount", false, "MY_CUSTOMERS_COUNT");
        public final static Property AuthUpdateTime = new Property(22, String.class, "authUpdateTime", false, "AUTH_UPDATE_TIME");
        public final static Property InviteCode = new Property(23, String.class, "inviteCode", false, "INVITE_CODE");
        public final static Property CompletedOrderAmountCount = new Property(24, String.class, "completedOrderAmountCount", false, "COMPLETED_ORDER_AMOUNT_COUNT");
        public final static Property IsExtension = new Property(25, String.class, "isExtension", false, "IS_EXTENSION");
    }


    public ToBBeanDao(DaoConfig config) {
        super(config);
    }
    
    public ToBBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TO_BBEAN\" (" + //
                "\"ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: id
                "\"ORGANIZATION_NAME\" TEXT," + // 1: organizationName
                "\"PREPAREDFOR_NUM\" INTEGER NOT NULL ," + // 2: preparedforNum
                "\"SUBMIT_TIME\" TEXT," + // 3: submitTime
                "\"TEAM_NUM\" INTEGER NOT NULL ," + // 4: teamNum
                "\"ORGANIZATION_ID\" TEXT," + // 5: organizationId
                "\"BEAT_RANK\" INTEGER NOT NULL ," + // 6: beatRank
                "\"ADVISER_PHOTO\" TEXT," + // 7: adviserPhoto
                "\"ADVISER_STATUS\" INTEGER NOT NULL ," + // 8: adviserStatus
                "\"ADVISER_ORGANIZATION\" TEXT," + // 9: adviserOrganization
                "\"ADVISER_NAME\" TEXT," + // 10: adviserName
                "\"ADVISER_LEVEL\" INTEGER NOT NULL ," + // 11: adviserLevel
                "\"IS_EMPLOYEE\" TEXT," + // 12: isEmployee
                "\"ADVISER_NUMBER\" TEXT," + // 13: adviserNumber
                "\"COMPLETED_ORDER_COUNT\" INTEGER NOT NULL ," + // 14: completedOrderCount
                "\"REJECT_REASON\" TEXT," + // 15: rejectReason
                "\"SERVICE_CHECK_STATUS\" INTEGER NOT NULL ," + // 16: serviceCheckStatus
                "\"ADVISER_PERFORMANCE\" TEXT," + // 17: adviserPerformance
                "\"ADVISER_STATE\" TEXT," + // 18: adviserState
                "\"CATEGORY\" TEXT," + // 19: category
                "\"ADVISER_RANK\" TEXT," + // 20: adviserRank
                "\"MY_CUSTOMERS_COUNT\" INTEGER NOT NULL ," + // 21: myCustomersCount
                "\"AUTH_UPDATE_TIME\" TEXT," + // 22: authUpdateTime
                "\"INVITE_CODE\" TEXT," + // 23: inviteCode
                "\"COMPLETED_ORDER_AMOUNT_COUNT\" TEXT," + // 24: completedOrderAmountCount
                "\"IS_EXTENSION\" TEXT);"); // 25: isExtension
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TO_BBEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, ToBBean entity) {
        stmt.clearBindings();
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(1, id);
        }
 
        String organizationName = entity.getOrganizationName();
        if (organizationName != null) {
            stmt.bindString(2, organizationName);
        }
        stmt.bindLong(3, entity.getPreparedforNum());
 
        String submitTime = entity.getSubmitTime();
        if (submitTime != null) {
            stmt.bindString(4, submitTime);
        }
        stmt.bindLong(5, entity.getTeamNum());
 
        String organizationId = entity.getOrganizationId();
        if (organizationId != null) {
            stmt.bindString(6, organizationId);
        }
        stmt.bindLong(7, entity.getBeatRank());
 
        String adviserPhoto = entity.getAdviserPhoto();
        if (adviserPhoto != null) {
            stmt.bindString(8, adviserPhoto);
        }
        stmt.bindLong(9, entity.getAdviserStatus());
 
        String adviserOrganization = entity.getAdviserOrganization();
        if (adviserOrganization != null) {
            stmt.bindString(10, adviserOrganization);
        }
 
        String adviserName = entity.getAdviserName();
        if (adviserName != null) {
            stmt.bindString(11, adviserName);
        }
        stmt.bindLong(12, entity.getAdviserLevel());
 
        String isEmployee = entity.getIsEmployee();
        if (isEmployee != null) {
            stmt.bindString(13, isEmployee);
        }
 
        String adviserNumber = entity.getAdviserNumber();
        if (adviserNumber != null) {
            stmt.bindString(14, adviserNumber);
        }
        stmt.bindLong(15, entity.getCompletedOrderCount());
 
        String rejectReason = entity.getRejectReason();
        if (rejectReason != null) {
            stmt.bindString(16, rejectReason);
        }
        stmt.bindLong(17, entity.getServiceCheckStatus());
 
        String adviserPerformance = entity.getAdviserPerformance();
        if (adviserPerformance != null) {
            stmt.bindString(18, adviserPerformance);
        }
 
        String adviserState = entity.getAdviserState();
        if (adviserState != null) {
            stmt.bindString(19, adviserState);
        }
 
        String category = entity.getCategory();
        if (category != null) {
            stmt.bindString(20, category);
        }
 
        String adviserRank = entity.getAdviserRank();
        if (adviserRank != null) {
            stmt.bindString(21, adviserRank);
        }
        stmt.bindLong(22, entity.getMyCustomersCount());
 
        String authUpdateTime = entity.getAuthUpdateTime();
        if (authUpdateTime != null) {
            stmt.bindString(23, authUpdateTime);
        }
 
        String inviteCode = entity.getInviteCode();
        if (inviteCode != null) {
            stmt.bindString(24, inviteCode);
        }
 
        String completedOrderAmountCount = entity.getCompletedOrderAmountCount();
        if (completedOrderAmountCount != null) {
            stmt.bindString(25, completedOrderAmountCount);
        }
 
        String isExtension = entity.getIsExtension();
        if (isExtension != null) {
            stmt.bindString(26, isExtension);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, ToBBean entity) {
        stmt.clearBindings();
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(1, id);
        }
 
        String organizationName = entity.getOrganizationName();
        if (organizationName != null) {
            stmt.bindString(2, organizationName);
        }
        stmt.bindLong(3, entity.getPreparedforNum());
 
        String submitTime = entity.getSubmitTime();
        if (submitTime != null) {
            stmt.bindString(4, submitTime);
        }
        stmt.bindLong(5, entity.getTeamNum());
 
        String organizationId = entity.getOrganizationId();
        if (organizationId != null) {
            stmt.bindString(6, organizationId);
        }
        stmt.bindLong(7, entity.getBeatRank());
 
        String adviserPhoto = entity.getAdviserPhoto();
        if (adviserPhoto != null) {
            stmt.bindString(8, adviserPhoto);
        }
        stmt.bindLong(9, entity.getAdviserStatus());
 
        String adviserOrganization = entity.getAdviserOrganization();
        if (adviserOrganization != null) {
            stmt.bindString(10, adviserOrganization);
        }
 
        String adviserName = entity.getAdviserName();
        if (adviserName != null) {
            stmt.bindString(11, adviserName);
        }
        stmt.bindLong(12, entity.getAdviserLevel());
 
        String isEmployee = entity.getIsEmployee();
        if (isEmployee != null) {
            stmt.bindString(13, isEmployee);
        }
 
        String adviserNumber = entity.getAdviserNumber();
        if (adviserNumber != null) {
            stmt.bindString(14, adviserNumber);
        }
        stmt.bindLong(15, entity.getCompletedOrderCount());
 
        String rejectReason = entity.getRejectReason();
        if (rejectReason != null) {
            stmt.bindString(16, rejectReason);
        }
        stmt.bindLong(17, entity.getServiceCheckStatus());
 
        String adviserPerformance = entity.getAdviserPerformance();
        if (adviserPerformance != null) {
            stmt.bindString(18, adviserPerformance);
        }
 
        String adviserState = entity.getAdviserState();
        if (adviserState != null) {
            stmt.bindString(19, adviserState);
        }
 
        String category = entity.getCategory();
        if (category != null) {
            stmt.bindString(20, category);
        }
 
        String adviserRank = entity.getAdviserRank();
        if (adviserRank != null) {
            stmt.bindString(21, adviserRank);
        }
        stmt.bindLong(22, entity.getMyCustomersCount());
 
        String authUpdateTime = entity.getAuthUpdateTime();
        if (authUpdateTime != null) {
            stmt.bindString(23, authUpdateTime);
        }
 
        String inviteCode = entity.getInviteCode();
        if (inviteCode != null) {
            stmt.bindString(24, inviteCode);
        }
 
        String completedOrderAmountCount = entity.getCompletedOrderAmountCount();
        if (completedOrderAmountCount != null) {
            stmt.bindString(25, completedOrderAmountCount);
        }
 
        String isExtension = entity.getIsExtension();
        if (isExtension != null) {
            stmt.bindString(26, isExtension);
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public ToBBean readEntity(Cursor cursor, int offset) {
        ToBBean entity = new ToBBean( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // organizationName
            cursor.getInt(offset + 2), // preparedforNum
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // submitTime
            cursor.getInt(offset + 4), // teamNum
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // organizationId
            cursor.getInt(offset + 6), // beatRank
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // adviserPhoto
            cursor.getInt(offset + 8), // adviserStatus
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // adviserOrganization
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // adviserName
            cursor.getInt(offset + 11), // adviserLevel
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // isEmployee
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // adviserNumber
            cursor.getInt(offset + 14), // completedOrderCount
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // rejectReason
            cursor.getInt(offset + 16), // serviceCheckStatus
            cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17), // adviserPerformance
            cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18), // adviserState
            cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19), // category
            cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20), // adviserRank
            cursor.getInt(offset + 21), // myCustomersCount
            cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22), // authUpdateTime
            cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23), // inviteCode
            cursor.isNull(offset + 24) ? null : cursor.getString(offset + 24), // completedOrderAmountCount
            cursor.isNull(offset + 25) ? null : cursor.getString(offset + 25) // isExtension
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, ToBBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setOrganizationName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setPreparedforNum(cursor.getInt(offset + 2));
        entity.setSubmitTime(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setTeamNum(cursor.getInt(offset + 4));
        entity.setOrganizationId(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setBeatRank(cursor.getInt(offset + 6));
        entity.setAdviserPhoto(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setAdviserStatus(cursor.getInt(offset + 8));
        entity.setAdviserOrganization(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setAdviserName(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setAdviserLevel(cursor.getInt(offset + 11));
        entity.setIsEmployee(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setAdviserNumber(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setCompletedOrderCount(cursor.getInt(offset + 14));
        entity.setRejectReason(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setServiceCheckStatus(cursor.getInt(offset + 16));
        entity.setAdviserPerformance(cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17));
        entity.setAdviserState(cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18));
        entity.setCategory(cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19));
        entity.setAdviserRank(cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20));
        entity.setMyCustomersCount(cursor.getInt(offset + 21));
        entity.setAuthUpdateTime(cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22));
        entity.setInviteCode(cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23));
        entity.setCompletedOrderAmountCount(cursor.isNull(offset + 24) ? null : cursor.getString(offset + 24));
        entity.setIsExtension(cursor.isNull(offset + 25) ? null : cursor.getString(offset + 25));
     }
    
    @Override
    protected final String updateKeyAfterInsert(ToBBean entity, long rowId) {
        return entity.getId();
    }
    
    @Override
    public String getKey(ToBBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(ToBBean entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}

package com.example.sqlitetest.db;

import android.content.Context;

import com.example.sqlitetest.MyApplication;
import com.example.sqlitetest.R;

/**
 * Created by emery on 2017/5/10.
 */

public final class SqlBox {
    Context context = MyApplication.getAppContext();
    private static long nShopID = -1;
    private static long nUserID = -1;
    private String sFilterTxt = "";
    private String sDecimalPointSetting = "2";
    private String sql = "Select t0.[_id] as _id,'"
            + context.getString(R.string.receivable_partner_customer)
            + "' as sbPartnerNameLabel,t0.[sName] as sName, '"
            + context.getString(R.string.receivable_partner_Amount)
            + "' as sbPartnerAmountLabel,t1.[fBPartnerAmount] as fBPartnerAmount, '"
            + context.getString(R.string.receivable_partner_Received)
            + "' as sbPartnerReceivedLabel, t1.[fBPartnerReceived] as fBPartnerReceived,'"
            + context.getString(R.string.receivable_partner_DueAmount)
            + "' as sbPartnerDueAmountLabel, t1.[fBPartnerDueAmount] as fBPartnerDueAmount " +
            "FROM  "
            + " (SELECT [T_BPARTNER].[_id] as _id,[T_BPARTNER].[sName] as sName FROM " +
            "[T_BPARTNER]"
            + "  WHERE [T_BPARTNER].[nShopID]= "
            + nShopID
            + "  AND [T_BPARTNER].[bIsActive]='Y' AND [T_BPARTNER].[bIsCustomer]='Y'"
            + "  AND [T_BPARTNER].[sName] LIKE '%"
            + sFilterTxt
            + "%') as t0 "
            + "  LEFT JOIN   "
            + " (SELECT [T_PRODUCTDOC].[nBPartnerID] as nBPartnerID, "
            + " sum(case when [T_PRODUCTDOC].[nProductTransacType]=100001 then fAmount when  "
            + " [T_PRODUCTDOC].[nProductTransacType]=100015 then -1*fAmount else 0 end ) as " +
            "fBPartnerAmount,  "
            + " sum(case when [T_PRODUCTDOC].[nProductTransacType]=100001 or [T_PRODUCTDOC]" +
            ".[nProductTransacType]=100017 then "
            + " [T_PRODUCTDOC].[fReceived] when [T_PRODUCTDOC].[nProductTransacType]=100015 " +
            "then -1*[T_PRODUCTDOC].[fReceived] else 0 end)"
            + " as fBPartnerReceived,"
            + " round((sum(case when [T_PRODUCTDOC].[nProductTransacType]=100001 then " +
            "[T_PRODUCTDOC].[fAmount]   when [T_PRODUCTDOC].[nProductTransacType]=100015 then" +
            " -1*[T_PRODUCTDOC].[fAmount] when [T_PRODUCTDOC].[nProductTransacType]=100017 " +
            "then  [T_PRODUCTDOC].[fAmount]  else 0 end ) -  "
            + " sum(case when [T_PRODUCTDOC].[nProductTransacType]=100001 then [T_PRODUCTDOC]" +
            ".[fReceived]   when [T_PRODUCTDOC].[nProductTransacType]=100015 then " +
            "-1*[T_PRODUCTDOC].[fReceived] when [T_PRODUCTDOC].[nProductTransacType]=100017 " +
            "then  [T_PRODUCTDOC].[fReceived]  else 0 end )), "
            + sDecimalPointSetting
            + ") as fBPartnerDueAmount  "
            + " FROM [T_PRODUCTDOC]"
            + " WHERE [T_PRODUCTDOC].[nShopID]=  "
            + nShopID
            + "  AND   "
            + " ([T_PRODUCTDOC].[nProductTransacType]=100001 or [T_PRODUCTDOC]" +
            ".[nProductTransacType]=100015 or "
            + " [T_PRODUCTDOC].[nProductTransacType]=100017) AND ([T_PRODUCTDOC]" +
            ".[nDeletionFlag] is null or [T_PRODUCTDOC].[nDeletionFlag]<>1)"
            + " group by nBPartnerID) as t1  on t1.[nBPartnerID] = t0.[_id]";

    public static final String CHECKTABLECOUNT = "select * from " + UserDao.mTableName;
    public static final String QUERYPRODUCTDOC = "select round ( sum(case when [T_PRODUCTDOC]" +
            ".[nProductTransacType]=100001 then [T_PRODUCTDOC].[nProductQty]    when   " +
            "[T_PRODUCTDOC].[nProductTransacType]=100015 then -1*[T_PRODUCTDOC].[nProductQty]  " +
            "else 0 end) ,2) as nProductQty" +
            "    from T_PRODUCTDOC" +
            "    where  ([T_PRODUCTDOC].[nDeletionFlag] is null or [T_PRODUCTDOC]" +
            ".[nDeletionFlag]<>1) and   [T_PRODUCTDOC].[nShopId]=143811";

    public static final String ORDERBY = " select nProductQty" +
            "    from T_PRODUCTDOC" +
            "    where [T_PRODUCTDOC].[nShopId]=143811" +
            "    order by" +
            "    nDateTime desc";

    public static final String LIMIT = "select nProductQty" +
            "    from T_PRODUCTDOC" +
            "    where [T_PRODUCTDOC].[nShopId]=143811 ";

    public static final String GROUPBY = "select *" +
            "    from T_PRODUCTDOC" +
            "    where [T_PRODUCTDOC].[nShopId]=143811" +
            "    group by" +
            "    sOrderNo ";
    public static final String INSERT="INSERT INTO T_PRODUCTDOC (_id,nshopID) values (?,?)";

}

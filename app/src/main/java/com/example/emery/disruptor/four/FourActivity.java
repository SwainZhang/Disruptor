package com.example.emery.disruptor.four;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.emery.disruptor.MyApplication;
import com.example.emery.disruptor.R;
import com.example.emery.disruptor.event.QueryResult;
import com.example.emery.disruptor.first.WorkerThreadFactory;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static android.os.Environment.getExternalStorageDirectory;

/**
 * Created by emery on 2017/5/7.
 * <p>
 * 插入10000条350ms
 */

public class FourActivity extends AppCompatActivity {
    Disruptor<QueryEvent> mQueryEventDisruptor;
    private Producer mProducer;
    private RecyclerView mRecyclerView;
    private ArrayList<User> mUsers;
    private UserAdapter mUserAdapter;

    Disruptor<InsertEvent> mInsertEventDisruptor;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three);
        mRecyclerView = (RecyclerView) findViewById(R.id.rl_user);
        mUsers = new ArrayList<>();
        mUserAdapter = new UserAdapter(mUsers, this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mUserAdapter);
        EventBus.getDefault().register(this);
    }

    public  void tablet(View view ) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileInputStream open=null;
                FileOutputStream fos=null;
                try {
                    open = (FileInputStream) getAssets().open("laiqian.db");
                    String dsnt=Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"test"+File.separator+"laiqian_bak.db";
                    File file=new File(dsnt);
                    if(!file.exists()){
                       file.mkdir();
                    }
                    fos=new FileOutputStream(file.getAbsolutePath());

                    byte [] buffer=new byte[1024];
                    int length=0;
                    while ((length=open.read(buffer))!=-1){
                        fos.write(buffer,0,length);
                        fos.flush();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    System.out.println("来钱快db复制到平板成功");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MyApplication.getAppContext(),"来钱快db复制到平板成功",Toast.LENGTH_SHORT).show();
                        }
                    });
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        open.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    public void insert(View view) throws IOException {
        //batchInsert();
       UserDao.getInstance().insert();
    }

    private void batchInsert() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long start = System.currentTimeMillis();
                mInsertEventDisruptor = new Disruptor<InsertEvent>(
                        new InsertEventFactory(), 1024,
                        new WorkerThreadFactory("insert"),
                        ProducerType.MULTI,
                        new YieldingWaitStrategy());
                EventHandlerGroup insertEventHandlerGroup = mInsertEventDisruptor
                        .handleEventsWithWorkerPool(
                                new InsertEventHandler(),
                                new InsertEventHandler(),
                                new InsertEventHandler(),
                                new InsertEventHandler(),
                                new InsertEventHandler(),
                                new InsertEventHandler(),
                                new InsertEventHandler(),
                                new InsertEventHandler(),
                                new InsertEventHandler(),
                                new InsertEventHandler());

                insertEventHandlerGroup.then(new EndInserHandler());
                mInsertEventDisruptor.start();

                InsertEvent insertEvent = new InsertEvent();

                insertEvent.tableName = UserDao.mTableName;
                insertEvent.nullColumnHack = null;
                UserDao dao = UserDao.getInstance();
                dao.beginTransactionNonExclusive();
                for (int i = 0; i < 10; i++) {
                    ContentValues contentValues=new ContentValues();
                    contentValues.put(TableColums.NAME, "name二" + i);
                    contentValues.put(TableColums.AGE, "age二" + i);
                    contentValues.put(TableColums.ADDRESS, "address二" + i);
                    contentValues.put(TableColums.PHONE,"phone二" + i);
                    insertEvent.index=String.valueOf(i);
                    insertEvent.mContentValues=contentValues;
                    dao.insert(insertEvent);
                }
                dao.endTransaction();

                System.out.println("插入耗时:" + (System.currentTimeMillis() - start));

            }
        }).start();
    }

    public void query(View view) throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                QueryEvent queryEvent = new QueryEvent();
                queryEvent.colums = null;
                queryEvent.selection = null;
                queryEvent.selectionArgs = null;
                queryEvent.groupBy = null;
                queryEvent.having = null;
                queryEvent.orderBy = TableColums.AGE + " desc";
                queryEvent.limit = 10000;
                queryEvent.table = Producer.mTableName;
                UserDao dao = UserDao.getInstance();
                //模拟很多个任务，而不是执行单个任务，实际上查询10，100，1000条数据这个任务还是在子线程里面完成。Disruptor做的是
                //将阻塞队列改成了无锁的RingBuffer。有个想法就是如果我们要查询50条数据，可以再使用多个线程来完成这个50条数据的查询。
                for (int i = 0; i<1; i++) {
                    queryEvent.offset =i * queryEvent.limit;
                    queryEvent.index = i;
                    dao.query(queryEvent);
                }
            }
        }).start();

    }

    public void delete(View view) {
        UserDao.getInstance().deleteBak();
    }

    public void clear(View view) {
        mUsers.clear();
        mUserAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void sort(View view) {
        Collections.sort(mUsers, new MyComparetor());
        mUserAdapter.notifyDataSetChanged();
        for (User user : mUsers) {
            System.out.println("" + user.getId());
        }

    }
    public void copy(View view) throws IOException {

        new Thread(new Runnable() {
            @Override
            public void run() {
               String path= "/data/data/com.example.emery.disruptor/databases/dis.db";
                    File databasePath =new File(UserDao.getInstance().getDatabasepath());
                    File databaseCopyPath = new File(getExternalStorageDirectory().getAbsolutePath()+File.separator+"dis_bak.db");
                    if(databaseCopyPath.exists()){
                        databasePath.delete();
                    }
                    long dataLenth = databasePath.length();
                    long length = databaseCopyPath.length();

                    System.out.println("======databasepath======="+databasePath.getAbsolutePath());
                FileInputStream inputStream=null;
                FileOutputStream outputStream = null;
                try {
                    inputStream=new FileInputStream(databasePath);
                    outputStream=new FileOutputStream(databaseCopyPath);
                    byte[] buf=new byte[1024];
                    int line =0;
                    while ((line=inputStream.read(buf))!=-1){
                        outputStream.write(buf,0,line);
                        outputStream.flush();
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    System.out.println(""+e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println(""+e.getMessage());
                }finally {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase
                            (databaseCopyPath, null);
                    final Cursor cursor = sqLiteDatabase.rawQuery("select * from " + UserDao
                            .mTableName, null);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MyApplication.getAppContext(),"备份数据库有"+cursor.getCount(),Toast.LENGTH_SHORT);
                       cursor.close();
                        }
                    });
                    System.out.println("备份完成！！！");
                }


              /*  try {
                        mReader = new FileReader(databasePath);
                        mWriter = new FileWriter(databaseCopyPath);
                        char[] buf = new char[1024];
                        int line = -1;
                        while ((line = mReader.read(buf)) != -1) {
                            mWriter.write(buf, 0, line);
                            mWriter.flush();
                        }
                    }catch (Exception e){
                        System.out.println("IO Exception "+e.getMessage() );
                    }finally {
                        try {
                            mWriter.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            mReader.close();
                            isFinished=true;
                            System.out.println("备份完成！");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                }*/
            }
        }).start();


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChanged(QueryResult queryResult) {
        mUsers.addAll(queryResult.getUsers());
        mUserAdapter.notifyDataSetChanged();
    }
}

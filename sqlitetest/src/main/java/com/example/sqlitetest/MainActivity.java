package com.example.sqlitetest;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.sqlitetest.db.MyComparetor;
import com.example.sqlitetest.db.QueryEvent;
import com.example.sqlitetest.db.SqlBox;
import com.example.sqlitetest.db.TableColums;
import com.example.sqlitetest.db.User;
import com.example.sqlitetest.db.UserAdapter;
import com.example.sqlitetest.db.UserDao;
import com.example.sqlitetest.event.QueryResult;
import com.lmax.disruptor.dsl.Disruptor;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

import static android.os.Environment.getExternalStorageDirectory;

/**
 *
 *  20条数据  group by单线程耗时2740ms左右，其中线程休眠12ms(模拟一千条数据解析）
 *  20条数据  group by多线程拆分，耗时最长的线程4640ms,其中线程休眠5ms。最短的耗时2200ms,其中线程休眠3ms。
 *
 *
 *  20条数据  limit 单线程耗时2043ms,其中线程休眠7ms
 *  20条数据  limit 多线程拆分，耗时最长的线程130ms,其中线程休眠9ms。耗时最短的线程44ms,其中线程休眠3ms

 *  20条数据  order by 单线程耗时1913ms,其中线程耗时5ms
 *  20条数据  order by 多线程拆分，耗时最长的线程57ms,线程休眠3ms,耗时最短的30ms,线程休眠6ms
 *
 *有时候统计的时候group by没有limit：
 *
 *   73832条中group by 耗时28400ms
 *   10000条中group by 耗时2250ms
 *
 *
 *
 */
public class MainActivity extends AppCompatActivity {
    Disruptor<QueryEvent> mQueryEventDisruptor;
    private RecyclerView mRecyclerView;
    private ArrayList<User> mUsers;
    private UserAdapter mUserAdapter;
    private File mDatabaseCopyPath;
    private File mDatabasePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                InputStream open=null;
                FileOutputStream fos=null;
                try {
                    open = getAssets().open("laiqian.db");

                    //String dsnt= getExternalStorageDirectory().getAbsolutePath()+ File.separator+"test"+File.separator+"laiqian_bak.db";
                    String dsnt="/data/data/"+getPackageName()+"/databases"+"/laiqian.db";
                    File file=new File(dsnt);
                    if(file.exists()){
                        file.delete();
                    }
                    fos=new FileOutputStream(file.getAbsolutePath());

                    byte [] buffer=new byte[1024];
                    int length=0;
                    while ((length=open.read(buffer))!=-1){
                        fos.write(buffer,0,length);
                        fos.flush();
                        System.out.println("导入中。。。");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    System.out.println("来钱快导入到平板成功");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MyApplication.getAppContext(),"来钱快db导入到平板成功",Toast.LENGTH_SHORT).show();
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
        }).start();

    }

    public void insert(View view) throws IOException {
        //batchInsert();
        UserDao.getInstance().insert();
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
                queryEvent.limit = 20;
                queryEvent.table = UserDao.mTableName;
                queryEvent.sql= SqlBox.GROUPBY;
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
       // UserDao.getInstance().deleteBak();
        UserDao.getInstance().delete(UserDao.mTableName);
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
                mDatabasePath = new File(UserDao.getInstance().getDatabasepath());
                System.out.println("数据库大小--"+ android.text.format.Formatter.formatShortFileSize(MainActivity.this,mDatabasePath.length()));
                mDatabaseCopyPath = new File(getExternalStorageDirectory().getAbsolutePath()+File.separator+"laiqian_bak.db");
                if(mDatabaseCopyPath.exists()){
                    mDatabaseCopyPath.delete();
                }
                long dataLenth = mDatabasePath.length();
                long length = mDatabaseCopyPath.length();

                System.out.println("======databasepath======="+ mDatabasePath.getAbsolutePath());
                FileInputStream inputStream=null;
                FileOutputStream outputStream = null;
                try {
                    inputStream=new FileInputStream(mDatabasePath);
                    outputStream=new FileOutputStream(mDatabaseCopyPath);
                    byte[] buf=new byte[1024];
                    int line =0;
                    while ((line=inputStream.read(buf))!=-1){
                        outputStream.write(buf,0,line);
                        outputStream.flush();
                        Log.i("TAG","备份中。。。");
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

    public void openCopyDatabase(View view){
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase
                (mDatabaseCopyPath, null);
        final Cursor cursor = sqLiteDatabase.rawQuery("select * from " + UserDao
                .mTableName, null);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("备份数据库"+UserDao.mTableName+"有"+cursor.getCount()+"条数据！！");
                Toast.makeText(MyApplication.getAppContext(),"备份数据库有"+cursor.getCount(),Toast.LENGTH_SHORT);
                cursor.close();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChanged(QueryResult queryResult) {
        mUsers.addAll(queryResult.getUsers());
        mUserAdapter.notifyDataSetChanged();
    }

}

# HandlerLooper
handleLooper定时循环显示网络数据

1、网络权限
    <uses-permission android:name="android.permission.INTERNET"/>
    
2、布局
    <TextView
        android:id="@+id/tv_show"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_centerInParent="true"
        android:text="0"/>
3、代码

public class MainActivity extends AppCompatActivity {
    //从https://a4.plu.cn/api/ustream/home?version=3.6.1&device=4的主入口处得到roomID，替换url里roomID的数字即可,因为roomID会变化
    public String url = "http://liveapi.plu.cn/liveapp/admire?roomId=525613&count=0&version=3.6.0&device=4";
    private TextView tvShow;
    private Handler mHandler;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvShow = (TextView) findViewById(R.id.tv_show);

        //开启线程
        new LooperThread().start();
        //延迟3秒开始任务
        handler.postDelayed(task, 3000);
    }

    private final Runnable task = new Runnable() {

        public void run() {
            // TODO Auto-generated method stub
            if (true) {  //change to refresh if it is auto mod
                handler.postDelayed(this, 10000);
                
                //这里写你的功能模块代码
                //主线程中发消息
                getNetData();

            }
        }
    };

    //如何让自己的线程成为一个Looper线程?
    class LooperThread extends Thread {

        @Override
        public void run() {
            //①.准备成为Looper线程
            Looper.prepare();
            //②.要处理的业务逻辑
            mHandler = new Handler() {
                //工作线程中处理消息
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == 1) {
                        final int count = (int) msg.obj;
                        Log.i("TAG", "msg=" + count);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvShow.setText(String.valueOf(count));
                            }
                        });
                    }
                }
            };

            //③.进行循环
            Looper.loop();
        }
    }

    //获取网络数据
    private void getNetData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(url).build();
                    Response response = client.newCall(request).execute();
                    String str = response.body().string();
                    //json解析    {"result": 4656}
                    JSONObject jsonObject = new JSONObject(str);
                    String result = jsonObject.getString("result");
                    LiveHeart liveHeart = new LiveHeart();
                    liveHeart.setResult(Integer.parseInt(result));
                    //Log.i("==>>", "run: " + liveHeart.getResult());
                    int count = liveHeart.getResult();

                    Message msg = mHandler.obtainMessage(1, count);
                    mHandler.sendMessage(msg);

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}


注意总结：
postDelay中的1秒表示程序运行到此处延迟一秒开启这个handler
而Runnable中的10秒表示每隔10秒执行一次你的功能模块；
这个方法的弊端是一旦执行没有办法停止，直至程序退出。
        
    

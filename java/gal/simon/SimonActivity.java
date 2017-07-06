package gal.simon;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;


public class SimonActivity extends AppCompatActivity {

    final Random rand = new Random();
    int startlevel,currentlevel, currentStep, winLevel, score;
    String name;
    ArrayList<View> simon_buttons;
    ArrayList<View> game_progress;
    TextView messenger;
    TextView status;
    Handler handler;
    Object lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simon);

        simon_buttons = new ArrayList<View>();
        simon_buttons.add(findViewById(R.id.TopLeft));
        simon_buttons.add(findViewById(R.id.TopRight));
        simon_buttons.add(findViewById(R.id.BottomLeft));
        simon_buttons.add(findViewById(R.id.BottomRight));

        messenger = (TextView) findViewById(R.id.textMessenger);
        status = (TextView)findViewById(R.id.textStatus);

        lock = new Object();

        handler = new Handler(Looper.getMainLooper());
        startlevel = getIntent().getIntExtra("level",1);
        name = getIntent().getStringExtra("name");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startSimon();
            }
        }, 1000);
    }

    void startSimon() {
        game_progress = new ArrayList<View>();
        currentlevel=startlevel;
        winLevel=currentlevel+10;
        score = 0;
        feedProgress();
        playMessage("Game Starting in 3", 0, 1000);
        playMessage("Game Starting in 2", 1000, 2000);
        playMessage("Game Starting in 1", 2000, 3000);
        playProgress();

    }

    void playProgress() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (currentlevel != winLevel) {
                            synchronized (lock) {
                                try {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            status.setText("Level: "+currentlevel+", Score: "+score);
                                        }
                                    });
                                    currentStep = 0;
                                    playSteps();
                                    lock.wait();
                                }
                                catch (Exception e) {
                                }
                                finally {
                                    score+=currentlevel;
                                    ++currentlevel;
                                    if (currentlevel==winLevel){
                                        gameOverWin();
                                    }
                                }
                            }
                        }
                    }
                }).start();
            }
        },3500);
    }

    View.OnTouchListener myTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, final MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    view.setPressed(true);
                    break;

                case MotionEvent.ACTION_UP:
                    view.setPressed(false);
                    playSound(view);

                    if (view != game_progress.get(currentStep)) {
                        gameOverLose();
                        break;
                    }
                    if (currentStep == currentlevel - 1) {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                synchronized (lock) {
                                    lock.notify();
                                }
                            }
                        }, 1000);
                        playMessage("Great!", 0, 1000);
                    }
                    ++currentStep;
                    return false;

            }
            return true;
        }
    };
    void gameOverWin(){

        unsetClickables();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String toShare =  name + " " + score;
                SharedPreferences.Editor ed = StartActivity.shared.edit();
                ed.putString("winner",toShare);
            }
        });

        playMessage("Congratulations !!! you won...",0,2000);
        playMessage("Restarting in... 3",2000,3000);
        playMessage("Restarting in... 2",3000,4000);
        playMessage("Restarting in... 1",4000,5000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },5500);
    }

    void gameOverLose(){

        unsetClickables();

        playMessage("Game OVER !!!...",0,3000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(SimonActivity.this);
                builder.setMessage("Would you like to try again?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startSimon();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                               finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        },3000);
    }


    View.OnTouchListener NoneListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return false;
        }
    };

    void setClickables(){

        for (View v : simon_buttons)
            v.setOnTouchListener(myTouchListener);

    }

    void unsetClickables() {

        for (View v : simon_buttons)
            v.setOnTouchListener(NoneListener);
    }

    void playSteps(){
        unsetClickables();
        int inc = 0;
        for (int lev=0;lev<currentlevel;lev++) {
            final View view = game_progress.get(lev);

           handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    playSound(view);
                    view.setPressed(true);
                }
            }, (lev+1) * 1000 + inc);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.setPressed(false);
                }
            }, (lev+1) * 1000 + inc + 1000);

            if (lev==currentlevel-1){

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setClickables();
                    }
                }, (lev+1) * 1000 + inc + 1000);
            }

            inc+=500;
        }


    }

    void playMessage(final String msg,final int start ,final int lng){

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                messenger.setText(msg);
            }
        },start);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                messenger.setText("");
            }
        },lng);
    }

    void playSound(View v){


        int selected=0;
        switch (v.getTag().toString()){
            case "topLeft":
                selected = R.raw.doo;
                break;
            case "topRight":
                selected = R.raw.re;
                break;
            case "bottomLeft":
                selected = R.raw.fa;
                break;
            case "bottomRight":
                selected = R.raw.mi;
                break;
        }

        final MediaPlayer mp = MediaPlayer.create(getBaseContext(), selected);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.release();
            }

        });
        mp.start();

    }


    void feedProgress(){
        View value=null;
        if (game_progress.size()==winLevel)
            return;

        game_progress.add(simon_buttons.get(rand.nextInt(4)));
        feedProgress();
    }
}

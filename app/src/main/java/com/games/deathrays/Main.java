package com.games.deathrays;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;


public class Main extends Activity {
    public static final String APP_PREFERENCES = "GameSettings";    // название файла настроек
    //public static final String APP_PREFERENCES_SCORE = "Score";     // название поля в настройках
    public static final String APP_PREFERENCES_SPEED = "Speed";     // название поля в настройках
    public static final String APP_PREFERENCES_EASY_SCORES = "Easy";     // название поля в настройках
    public static final String APP_PREFERENCES_MEDIUM_SCORES = "Medium";     // название поля в настройках
    public static final String APP_PREFERENCES_HARD_SCORES = "Hard";     // название поля в настройках
    SharedPreferences preferences;      // настройки приложения
    private AdView adView;
    private Intent browserIntent;       // ссылки в about

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);  //получение или создание файла настроек на устройстве
        setContentView(R.layout.menu);
        getBackground();
        bannerUpdate();
    }

    protected void bannerUpdate() {
        // Поиск AdView как ресурса и отправка запроса.

        adView = (AdView) this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("414B7152B7AC5B329893D9AAF0C27296") // планшет
                .addTestDevice("B724471899A578B36755EBA32555D56A") // эмулятор
                .addTestDevice("C01F89B2B7F94D5E1E67FF3C55DA468A") // эмулятор
                .addTestDevice("8B5DA711E42B4E12E65DF35B6D4690B8") // nexus 7
                .build();
        adView.loadAd(adRequest);
    }

    private void getBackground() {
//        ImageView imageview = (ImageView) findViewById(R.id.imageView);
//        imageview.setImageResource(R.drawable.icon);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rays_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        try {
            findViewById(R.id.button_pause).callOnClick();
            findViewById(R.id.button_back).callOnClick();
//            ((GameView) findViewById(R.id.gameView)).changePauseState(); //меняем состояние паузы в gameview
        } catch (Exception e) {
            setException(e);
        }
        adView.pause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            findViewById(R.id.button_back).callOnClick();
//            ((GameView) findViewById(R.id.gameView)).changePauseState(); //меняем состояние паузы в gameview
        } catch (Exception e) {
            setException(e);
        }
        adView.resume();
    }

    @Override
    public void onDestroy() {
        adView.destroy();
        super.onDestroy();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            // кнопка старта
            case R.id.button_start:
                setContentView(R.layout.activity_game);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            // кнопка настроек
            case R.id.button_settings:
                setContentView(R.layout.settings);
                bannerUpdate();
                if (getSpeed() == 2) {
                    findViewById(R.id.easy).setBackgroundColor(Color.argb(255, 102, 152, 1));
                    findViewById(R.id.hard).setBackgroundColor(Color.argb(255, 85, 85, 85));
                    findViewById(R.id.medium).setBackgroundColor(Color.argb(255, 85, 85, 85));
                } else if (getSpeed() == 4) {
                    findViewById(R.id.medium).setBackgroundColor(Color.argb(255, 102, 152, 1));
                    findViewById(R.id.hard).setBackgroundColor(Color.argb(255, 85, 85, 85));
                    findViewById(R.id.easy).setBackgroundColor(Color.argb(255, 85, 85, 85));
                } else if (getSpeed() == 6) {
                    findViewById(R.id.hard).setBackgroundColor(Color.argb(255, 102, 152, 1));
                    findViewById(R.id.medium).setBackgroundColor(Color.argb(255, 85, 85, 85));
                    findViewById(R.id.easy).setBackgroundColor(Color.argb(255, 85, 85, 85));
                }
                break;
            // кнопка назад
            case R.id.button_back:
                setContentView(R.layout.menu);
                bannerUpdate();
                break;
            // кнопка очков
            case R.id.button_scores:
                setContentView(R.layout.scores);

                if (getSpeed() == 2) findViewById(R.id.EasyScores).setVisibility(View.VISIBLE);
                if (getSpeed() == 4) findViewById(R.id.MediumScores).setVisibility(View.VISIBLE);
                if (getSpeed() == 6) findViewById(R.id.HardScores).setVisibility(View.VISIBLE);

                ListView listScores = null;
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                        R.layout.my_list_item, getScores(getSpeed()));
                if (getSpeed() == 2) listScores = (ListView) findViewById(R.id.listView);
                else if (getSpeed() == 4) listScores = (ListView) findViewById(R.id.listView2);
                else if (getSpeed() == 6) listScores = (ListView) findViewById(R.id.listView3);
                if (listScores != null) {
                    listScores.setAdapter(adapter);
                }

                //bannerUpdate();
                break;
            // кнопка паузы
            case R.id.button_pause:
//                findViewById(R.id.gameMenu).setVisibility(View.GONE);   //скрываем pause
                findViewById(R.id.pauseMenu).setVisibility(View.VISIBLE);   //показываем экран паузы
                ((GameView) findViewById(R.id.gameView)).changePauseState(); //меняем состояние паузы в gameview
//                bannerUpdate();
                break;
            // кнопка возобновления игры
            case R.id.button_resume:
                findViewById(R.id.gameMenu).setVisibility(View.VISIBLE); // то же что в паузе, только наоборот
                findViewById(R.id.pauseMenu).setVisibility(View.GONE);
                ((GameView) findViewById(R.id.gameView)).changePauseState();
                break;
            // выход
            case R.id.button_exit:
                getIntent().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                break;
            // О программе
            case R.id.button_about:
                setContentView(R.layout.about);
                break;
            case R.id.button_vk:
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/nortorixsoft"));
                startActivity(browserIntent);
                break;
            case R.id.button_blogger:
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://nortorix.blogspot.ru/"));
                startActivity(browserIntent);
                break;
            case R.id.button_twitter:
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/Nortorix_Soft"));
                startActivity(browserIntent);
                break;
            // Назад к настройкам
            case R.id.button_back_about:
                setContentView(R.layout.settings);
                bannerUpdate();
                if (getSpeed() == 2) {
                    findViewById(R.id.easy).setBackgroundColor(Color.argb(255, 102, 152, 1));
                    findViewById(R.id.hard).setBackgroundColor(Color.argb(255, 85, 85, 85));
                    findViewById(R.id.medium).setBackgroundColor(Color.argb(255, 85, 85, 85));
                } else if (getSpeed() == 4) {
                    findViewById(R.id.medium).setBackgroundColor(Color.argb(255, 102, 152, 1));
                    findViewById(R.id.hard).setBackgroundColor(Color.argb(255, 85, 85, 85));
                    findViewById(R.id.easy).setBackgroundColor(Color.argb(255, 85, 85, 85));
                } else if (getSpeed() == 6) {
                    findViewById(R.id.hard).setBackgroundColor(Color.argb(255, 102, 152, 1));
                    findViewById(R.id.medium).setBackgroundColor(Color.argb(255, 85, 85, 85));
                    findViewById(R.id.easy).setBackgroundColor(Color.argb(255, 85, 85, 85));
                }
                break;

            case R.id.easy:
                setSpeed(2);
                findViewById(R.id.easy).setBackgroundColor(Color.argb(255, 102, 152, 1));
                findViewById(R.id.hard).setBackgroundColor(Color.argb(255, 85, 85, 85));
                findViewById(R.id.medium).setBackgroundColor(Color.argb(255, 85, 85, 85));
                break;
            case R.id.medium:
                setSpeed(4);
                findViewById(R.id.medium).setBackgroundColor(Color.argb(255, 102, 152, 1));
                findViewById(R.id.easy).setBackgroundColor(Color.argb(255, 85, 85, 85));
                findViewById(R.id.hard).setBackgroundColor(Color.argb(255, 85, 85, 85));
                break;
            case R.id.hard:
                setSpeed(6);
                findViewById(R.id.hard).setBackgroundColor(Color.argb(255, 102, 152, 1));
                findViewById(R.id.easy).setBackgroundColor(Color.argb(255, 85, 85, 85));
                findViewById(R.id.medium).setBackgroundColor(Color.argb(255, 85, 85, 85));
                break;

            default:
                break;
        }
    }

    // метод получения записанных очков из файла настроек
    public int getMaxScore(int Difficult) {
        int result;
//        if (preferences.contains(APP_PREFERENCES_SCORE)) { //если поле в файле настроек существует
//            result = preferences.getInt(APP_PREFERENCES_SCORE, 0); // 0 - стандартное значение, если че-то не так - поставит 0
//        } else result = 0;  // если не существует вернет 0
        if (getScores(Difficult).size() > 0)
            result = Integer.parseInt(getScores(Difficult).get(0).split(" ")[0]);
        else result = 0;
        return result;
    }

//    // метод записи очков в файл настроек
//    public void setMaxScore(int score) {
//        SharedPreferences.Editor editor = preferences.edit(); // создаем редактор для настроек
//        editor.putInt(APP_PREFERENCES_SCORE, score); // записываем в поле строку
//        editor.apply(); // применяем
//    }


    public ArrayList<String> getScores(int Difficult) {
        ArrayList<String> result = new ArrayList<String>();
        String param;
        switch (Difficult) {
            case 2:
                param = APP_PREFERENCES_EASY_SCORES;
                break;
            case 4:
                param = APP_PREFERENCES_MEDIUM_SCORES;
                break;
            case 6:
                param = APP_PREFERENCES_HARD_SCORES;
                break;
            default:
                param = APP_PREFERENCES_EASY_SCORES;
                break;
        }
        if (preferences.contains(param)) { //если поле в файле настроек существует
            String temp = preferences.getString(param, "0"); // 0 - стандартное значение, если че-то не так - поставит 0
            String[] str = temp.split(":");

            for (int i = 0; i < str.length; i++) {
                if (str[i].length() > 2) result.add(str[i]);
            }
        }
        if (result.size() == 0) {
            result = new ArrayList<String>();
        }
        return result;
    }

    // метод записи очков в файл настроек
    public void setScores(ArrayList<String> score, int Difficult) {
        String param, result = "";

        switch (Difficult) {
            case 2:
                param = APP_PREFERENCES_EASY_SCORES;
                break;
            case 4:
                param = APP_PREFERENCES_MEDIUM_SCORES;
                break;
            case 6:
                param = APP_PREFERENCES_HARD_SCORES;
                break;
            default:
                param = APP_PREFERENCES_EASY_SCORES;
                break;
        }

        SharedPreferences.Editor editor = preferences.edit(); // создаем редактор для настроек
        for (int i = 0; i < score.size(); i++) {
            result += ":" + score.get(i);
        }
        editor.putString(param, result); // записываем в поле строку
        editor.apply(); // применяем
    }


    // метод получения записанной скорости из файла настроек
    public int getSpeed() {
        int result;
        if (preferences.contains(APP_PREFERENCES_SPEED)) { //если поле в файле настроек существует
            result = preferences.getInt(APP_PREFERENCES_SPEED, 0); // 0 - стандартное значение, если че-то не так - поставит 0
        } else
            result = 2;  // если не существует вернет 2
        return result;
    }

    // метод записи скорости в файл настроек
    public void setSpeed(int speed) {
        SharedPreferences.Editor editor = preferences.edit(); // создаем редактор для настроек
        editor.putInt(APP_PREFERENCES_SPEED, speed); // записываем в поле строку
        editor.apply(); // применяем
    }

    public void onBackPressed() {
//        ((GameView) findViewById(R.id.gameView)).finish(); // вызываем завершение gameview
        setContentView(R.layout.menu); // выводим меню
    }

    private void setException(Exception e){
        System.out.println("Error! " + e);
    }
}

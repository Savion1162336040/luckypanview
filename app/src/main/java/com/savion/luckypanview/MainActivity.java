package com.savion.luckypanview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatButton button = findViewById(R.id.start);
        StaticPanView staticPanView = findViewById(R.id.staticpan);
        button.setOnClickListener(view -> {
            view.setEnabled(false);
            staticPanView.startRotate();
        });
        staticPanView.setAnimationEndListener(new StaticPanView.AnimationEndListener() {
            @Override
            public void endAnimation(int position, StaticPanView.StaticPanPojo panPojo) {
                Toast.makeText(getApplicationContext(), "选中:" + position, Toast.LENGTH_SHORT).show();
                button.setEnabled(true);
            }

            @Override
            public void renderAnimation(int position, StaticPanView.StaticPanPojo panPojo) {

            }
        });
        staticPanView.reset();
        staticPanView.setStaticPanPojo(new ColorData());
        staticPanView.setStaticPanPanProvider(new Data());
        staticPanView.invalidate();


    }

    private static class ColorData implements StaticPanView.StaticPanStylePojo {
        private String edgeColor;
        private List<String> colors = new ArrayList<>();

        public ColorData() {
            edgeColor = "#00ff00";
            colors.add("#aa00ff");
            colors.add("#bbffff");
            colors.add("#cc5577");
            colors.add("#dd8822");
        }

        @Override
        public String provideEdgeColor() {
            return edgeColor;
        }

        @Override
        public List<String> providePanColors() {
            return colors;
        }
    }

    private static class Data2 implements StaticPanView.StaticPanPojo {
        private float weight = 1;
        private String name;


        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setWeight(float weight) {
            this.weight = weight;
        }

        @Override
        public float luckyWeight() {
            return weight;
        }

        @Override
        public String luckyName() {
            return name;
        }
    }

    private class Data implements StaticPanView.StaticPanPanProvider {
        List<Data2> data2s = new ArrayList<>();

        public Data() {
            Data2 data2 = new Data2();
            data2.setName("name1111");
            data2.setWeight(1);
            data2s.add(data2);
            data2 = new Data2();
            data2.setName("name222");
            data2.setWeight(1);
            data2s.add(data2);
            data2 = new Data2();
            data2.setName("name3331");
            data2.setWeight(1);
            data2s.add(data2);
            data2 = new Data2();
            data2.setName("name4441");
            data2.setWeight(4);
            data2s.add(data2);
        }

        @Override
        public List<? extends StaticPanView.StaticPanPojo> providePans() {
            return data2s;
        }
    }
}
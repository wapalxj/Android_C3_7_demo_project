package vero.com.project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView mListView;
    private Button mBtn;
    private List<Question> questions;
    private MyAdapter myAdapter;
    private int[] myChoices;//我选择的答案
    private int[] res;//参考答案
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    private void initData() {
        questions=new ArrayList<>();
        Question q1=new Question("你正常吗?","0","1",0);
        Question q2=new Question("你好吗?","0","1",0);
        Question q3=new Question("你来了吗?","0","1",0);
        Question q4=new Question("你走了吗?","0","1",0);
        Question q5=new Question("你吃饭吗?","0","1",0);
        Question q6=new Question("你上班吗?","0","1",0);
        Question q7=new Question("你无聊吗?","0","1",0);
        Question q8=new Question("你吃饭吗?","0","1",0);
        Question q9=new Question("你上班吗?","0","1",0);
        Question q10=new Question("你无聊吗?","0","1",0);
        questions.add(q1);
        questions.add(q2);
        questions.add(q3);
        questions.add(q4);
        questions.add(q5);
        questions.add(q6);
        questions.add(q7);
        questions.add(q8);
        questions.add(q9);
        questions.add(q10);
        myChoices=new int[questions.size()];
        for (int i=0;i<myChoices.length;i++){
            myChoices[i]=-1;
        }
    }

    private void initView() {
        mListView= (ListView) findViewById(R.id.lv);
        mBtn= (Button) findViewById(R.id.btn);
        mBtn.setOnClickListener(this);
        myAdapter=new MyAdapter();
        mListView.setAdapter(myAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn:
                res=new int [questions.size()];
                for (int i=0;i<questions.size();i++){
                    res[i]=questions.get(i).getRes();
                }
                Intent intent=new Intent(MainActivity.this,Activity2.class);
                Bundle bundle=new Bundle();
                bundle.putString("myChoices",Arrays.toString(myChoices));
                bundle.putString("res",Arrays.toString(res));
                intent.putExtras(bundle);
                startActivity(intent);
                Toast.makeText(MainActivity.this, Arrays.toString(myChoices),Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return questions.size();
        }

        @Override
        public Object getItem(int position) {
            return questions.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view= null;
            ViewHolder holder=null;
            if (convertView==null){
                view= LayoutInflater.from(MainActivity.this).inflate(R.layout.item,null);
                holder=new ViewHolder();
                holder.TvQuestion= (TextView) view.findViewById(R.id.question);
                holder.rgb= (RadioGroup) view.findViewById(R.id.rgb);
                holder.rbLeft= (RadioButton) view.findViewById(R.id.rb_left);
                holder.rbRight= (RadioButton) view.findViewById(R.id.rb_right);
                view.setTag(holder);
            }else {
                view=convertView;
                holder= (ViewHolder) view.getTag();
            }
            holder.TvQuestion.setText((position+1)+"."+questions.get(position).getStrQuestion());
            holder.rbLeft.setText(questions.get(position).getRbLeft());
            holder.rbRight.setText(questions.get(position).getRbRight());
            holder.rgb.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId==R.id.rb_left){
                        myChoices[position]=0;
                    }else if (checkedId==R.id.rb_right){
                        myChoices[position]=1;
                    }
                }
            });
            holder.rbLeft.setChecked(myChoices[position]==0);
            holder.rbRight.setChecked(myChoices[position]==1);
            return view;
        }

        class ViewHolder{
            TextView TvQuestion;
            RadioGroup rgb;
            RadioButton rbLeft;
            RadioButton rbRight;
        }
    }
}

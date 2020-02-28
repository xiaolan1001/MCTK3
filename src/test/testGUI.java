/*
 * 功能：演示边界布局管理器：组件的位置和大小
 */
package test;

import javax.swing.*;
import java.awt.*;

/*Date: 2017年1月21日  Time: 下午4:59:40
@firstmiki ---blog.ppt1234.com*/

class TestBorderLayout extends JFrame{  //0.继承JFrame
    //1. 定义组件
    JButton jButton, jButton2,jButton3,jButton4,jButton5;

    public TestBorderLayout() {
        //2. 创建组件
        jButton = new JButton("中间");
        jButton2 = new JButton("北边");
        jButton3 = new JButton("西边");
        jButton4 = new JButton("东边");
        jButton5 = new JButton("南边");

        //3. 添加各个组件
        this.add(jButton, BorderLayout.CENTER);  //布局的中间
        this.add(jButton2, BorderLayout.NORTH);  //布局的北边
        this.add(jButton3, BorderLayout.WEST);   //布局的西边
        this.add(jButton4, BorderLayout.EAST);   //布局的东边
        this.add(jButton5, BorderLayout.SOUTH);  //布局的南边

        //4. 设置窗体属性
        this.setTitle("演示边界布局管理器");
        this.setSize(300, 200);
        this.setLocation(200, 200);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public static void main(String[] args) {
        TestBorderLayout testBorderLayout = new TestBorderLayout();

    }
}
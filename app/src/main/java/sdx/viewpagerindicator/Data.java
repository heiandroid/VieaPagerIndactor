package sdx.viewpagerindicator;

import java.util.List;

/**
 * Created by sdx on 2017/5/5.
 */

public class Data {

    /**
     * api_status : 1
     * data : {"item":[{"name":"重磅","engname":"adfa","start_color":"#ffffff","end_color":"#562362"},{"name":"重中","engname":"aaaaaa","start_color":"#236525","end_color":"#125698"},{"name":"观点","engname":"guandian","start_color":"#102365","end_color":"#23698a"},{"name":"好的等等","engname":"haodehahaha","start_color":"#fa2365","end_color":"#ab1236"}]}
     */

    public int api_status;
    public DataBean data;

    public int getApi_status() {
        return api_status;
    }

    public void setApi_status(int api_status) {
        this.api_status = api_status;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        public List<ItemBean> item;

        public List<ItemBean> getItem() {
            return item;
        }

        public void setItem(List<ItemBean> item) {
            this.item = item;
        }

        public static class ItemBean {
            /**
             * name : 重磅
             * engname : adfa
             * start_color : #ffffff
             * end_color : #562362
             */

            public String name;
            public String engname;
            public String start_color;
            public String end_color;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getEngname() {
                return engname;
            }

            public void setEngname(String engname) {
                this.engname = engname;
            }

            public String getStart_color() {
                return start_color;
            }

            public void setStart_color(String start_color) {
                this.start_color = start_color;
            }

            public String getEnd_color() {
                return end_color;
            }

            public void setEnd_color(String end_color) {
                this.end_color = end_color;
            }
        }
    }
}

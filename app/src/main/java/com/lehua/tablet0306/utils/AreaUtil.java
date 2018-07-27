package com.lehua.tablet0306.utils;

import android.content.Context;

import com.alibaba.fastjson.JSON;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lehua
 *         <p>
 *         只是一个工具类，专门用来处理 填写地址信息时的省，市，区的关联问题， 此类必须和文件 province_city_area.json
 *         绑定在一起，因为所有的省市区信息都存在这个 json文件中
 *         为了解析Json文件中的数据，我们专门定义了两个内部类，Province和City,这两个类Private的内部类，
 *         所以，我们在向外界提供数据时，不能提供Province,City形式的数据，只能是String形式
 *         <p>
 *         此类全是静态方法，为了向外界提供数据，提供了三个public方法
 *         List<String> getProvinces() ：获取所有省份名字
 *         List<String> getCities(String provinceName)：获取某个省份的所有城市
 *         List<String> getAreas(String provinceName, String cityName) ： 获取某个市的所有县/区
 */
public class AreaUtil {

    public static List<String> getProvinces(Context context) {
        List<String> provinces = null;
        if (getProvinceList(context) != null) {
            provinces = new ArrayList<>();
            for (Province p : getProvinceList(context))
                provinces.add(p.getName());
        }

        return provinces;
    }

    private static String getJsonData(Context context) {
        String content = null;
        try {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();

            InputStream ins = context.getAssets().open("province_city_area.json");
            BufferedInputStream bis = new BufferedInputStream(ins);
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = bis.read(buf)) > 0) {
                bao.write(buf, 0, len);
            }

            return bao.toString("utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }

    private static List<Province> getProvinceList(Context context) {
        List<Province> provinces = null;

        String content = getJsonData(context);
        if (content == null || content.equals("")) {
            return null;
        }
        provinces = JSON.parseArray(content, Province.class);
        return provinces;
    }

    /**
     * 根据省名字 查询其所有的城市
     *
     * @param provinceName
     * @return
     */
    private static List<City> getCityList(Context context, String provinceName) {
        List<City> cities = null;
        Province pv = null;
        if (getProvinceList(context) != null && getProvinceList(context).size() > 0) {
            for (Province p : getProvinceList(context)) {
                if (p.getName().equals(provinceName)) {
                    pv = p;
                    break;
                }
            }
        }
        if (pv == null) {
            return null;
        }

        return pv.getCitys();
    }

    public static List<String> getCities(Context context, String provinceName) {
        if (provinceName == null || provinceName.equals("")) {
            throw new IllegalArgumentException("参数provinceName 为空 ");
        }
        List<String> cities = null;

        if (getCityList(context, provinceName) != null && getCityList(context, provinceName).size() > 0) {
            cities = new ArrayList<>();
            for (City city : getCityList(context, provinceName))
                cities.add(city.getName());
        }
        return cities;
    }

    /**
     * 通过省名，城市名查询所有的区县
     *
     * @param provinceName
     * @param cityName
     * @return 按理说只要知道City, 就能查询查询到所有的区/县， 但其实所有的数据，都存在getProvinceList()方法获得的
     * List<Province>中， 故还是需要提供参数 provinceName
     */
    public static List<String> getAreas(Context context, String provinceName, String cityName) {
        List<String> areas = null;
        City city = null;

        if (provinceName == null || provinceName.equals("")) {
            throw new IllegalArgumentException("参数provinceName 为空 ");
        }

        if (cityName == null || cityName.equals("")) {
            throw new IllegalArgumentException("参数cityName 为空 ");
        }
        List<City> cities = getCityList(context, provinceName);
        if (cities != null && cities.size() > 0) {
            for (City c : cities) {
                if (c.getName().equals(cityName)) {
                    city = c;
                    break;
                }
            }
        }

        if (city != null) {
            return city.getAreas();
        }

        return null;
    }

    private static final class Province {
        private String name;
        private List<City> citys;

        public Province() {
            super();

        }

        public Province(String name) {
            super();
            this.name = name;
        }

        public Province(String name, List<City> citys) {
            super();
            this.name = name;
            this.citys = citys;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<City> getCitys() {
            return citys;
        }

        public void setCitys(List<City> citys) {
            this.citys = citys;
        }

        @Override
        public String toString() {
            return "Province [name=" + name + ", citys=" + citys + "]";
        }

    }

    private static final class City {
        private String name;
        private List<String> areas;

        public City() {
            super();

        }

        public City(String name) {
            super();
            this.name = name;
        }

        public City(String name, List<String> areas) {
            super();
            this.name = name;
            this.areas = areas;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getAreas() {
            return areas;
        }

        public void setAreas(List<String> areas) {
            this.areas = areas;
        }

        @Override
        public String toString() {
            return "City [name=" + name + ", areas=" + areas + "]";
        }

    }

}

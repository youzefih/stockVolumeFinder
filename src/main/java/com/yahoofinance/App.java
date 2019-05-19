package com.yahoofinance;

import pl.zankowski.iextrading4j.api.stocks.*;

import pl.zankowski.iextrading4j.client.IEXTradingClient;

import pl.zankowski.iextrading4j.client.rest.request.stocks.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.util.*;



public class App {
    private final IEXTradingClient iexTradingClient = IEXTradingClient.create();
    private LinkedList<String> item = new LinkedList<>();
    private int index = 0;
    private String[] arrays = new String[40];
    private String currentPrice = null;
    //private String stockName = null;
    private ArrayList sortingVol = new ArrayList();
    private ArrayList sortingName = new ArrayList();
    private ArrayList realCostPerShare = new ArrayList();

    private int curSize = 0;

    public static void main(String[] args) {

        App print = new App();
        double init = System.nanoTime();
        print.printerApp();
        double fin = System.nanoTime() - init;
        double secs = fin / 1000000000;
        System.out.println("Done! it took :" + secs + " seconds to perform the test with 3500 elements.");
    }

    public void printerApp() {
//        clearTxt();
//        stockList();
//        printingInOrder();
        sal();

    }

    public void sal(){
        Financials quote = null;
        try {
            quote = iexTradingClient.executeRequest(new FinancialsRequestBuilder().withSymbol("FB").build());
        } catch (pl.zankowski.iextrading4j.api.exception.IEXTradingException e) {

        }

        String value = String.valueOf(quote);
        System.out.println(quote);
        for (int i = 0; i < arrays.length; i++) {
            arrays[i] = value.split(",")[i];
            System.out.println(arrays[i]);
        }

    }

    public int profit() {

        double purchaseCost = 24.26;
        double quantity = 444;
        double totalCost = purchaseCost * quantity;
        double currentValue = Integer.valueOf(currentPrice);
        int profit = (int) (currentValue - totalCost);
        return profit;

    }

    public void stockList() {

        File file = new File("nasdaqlisted.txt");
        try {
            Scanner scan = new Scanner(file);
            while (scan.hasNext()) {
                item.add(scan.next());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //item.size()-3
        for (int i = 0; i < item.size(); i++) {
            try {
                int volume = getVolumePercent(item.get(i));
                String naming = item.get(i);
                sortingPercents(naming, volume, currentPrice);
                System.out.println("loading " + i + "/4000 stocks");
            } catch (java.lang.IndexOutOfBoundsException e) {
                System.out.println("error 404");
            }
        }

    }

    public void sortingPercents(String name, int volume, String price) {
        int index = binarySearch(volume, 0, curSize - 1);
        sortingVol.add(index, volume);
        sortingName.add(index, name);
        realCostPerShare.add(index, price);

        curSize++;

    }

    public void printingInOrder() {

        for (int i = 0; i < sortingVol.size()-2; i++) {


                    writeTotxt(sortingName.get(i) + " volume:   " + sortingVol.get(i) + "%  Cost per Share:  $" + realCostPerShare.get(i));



        }
    }

    private int binarySearch(int obj, int lo, int hi) {
        Comparator compare = Comparator.naturalOrder();

        if (hi < lo) return lo;
        int mid = (lo + hi) >> 1;
        int result = compare.compare(obj, sortingVol.get(mid));
        if (result >= 0) return binarySearch(obj, lo, mid - 1);  //go right
        return binarySearch(obj, mid + 1, hi);
    }


    public int getVolumePercent(String names) {
        index++;
        Quote quote = null;
        try {
            quote = iexTradingClient.executeRequest(new QuoteRequestBuilder().withSymbol(names).build());
        } catch (pl.zankowski.iextrading4j.api.exception.IEXTradingException e) {

            if (index == item.size()) {
                return -1;
            }
            return getVolumePercent(item.get(index));
        }

        String value = String.valueOf(quote);
        // System.out.println(value);
        for (int i = 0; i < arrays.length; i++) {
            arrays[i] = value.split(",")[i];
        }
        String values = (arrays[1] + "   " + arrays[17] + "    " + arrays[16] + "     " + arrays[31]);
        // 1 (company name ) ,16 (latest volume) , 17 (latest price realtime) , 31 ( avg total volum
        try {
            String latestVolume = arrays[16];
            String avg = arrays[31];
            String curPrice = arrays[17];
            StringTokenizer tokens = new StringTokenizer(latestVolume, "=");
            String volum = null;
            while (tokens.hasMoreElements()) {
                tokens.nextElement();
                volum = (String) tokens.nextElement();

            }
            int curVol = Integer.valueOf(volum);
            tokens = new StringTokenizer(avg, "=");
            volum = null;
            while (tokens.hasMoreElements()) {
                tokens.nextElement();
                volum = (String) tokens.nextElement();

            }
            int avgVol = Integer.valueOf(volum);
            tokens = new StringTokenizer(curPrice, "=");
            volum = null;
            while (tokens.hasMoreElements()) {
                tokens.nextElement();
                volum = (String) tokens.nextElement();
            }
            currentPrice = volum;
            //stockName = arrays[1];
            int VolumePercent = calculations(avgVol, curVol);
            return VolumePercent;

        } catch (java.lang.NumberFormatException e) {
            return -1;
        }


    }


    public int calculations(int averageVolume, int currentVolume) {  //returns percent difference between current volume and avg volume
        if (averageVolume == 0 || currentVolume == 0) {
            return -1;
        }
        int tmp = ((currentVolume * 100) / averageVolume) - 100;

        return tmp;
    }


    public void writeTotxt(String quote) {

        try {
            FileWriter write = new FileWriter("output.txt", true);
            write.write(quote + "\n");
            write.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearTxt() {
        try {
            FileWriter write = new FileWriter("output.txt", false);
        } catch (Exception e) {
            System.out.println("lol");
        }


    }


}

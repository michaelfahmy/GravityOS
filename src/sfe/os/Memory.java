package sfe.os;

import apps.Memo;
import sun.plugin2.gluegen.runtime.CPU;
class Frame {
    private int id;
    private String data;
    private int offset=0;
    Frame(int id,String data,int offset){
        this.id = id;
        this.data = data;
        this.offset=offset;
    }

    Frame(int id){
        this.id = id;
        this.data = "";
    }

    public void modify(String data){
        this.data = data;
    }

    public String read(){
        return data;
    }

    public int getId(){
        return id;
    }
}


public class Memory {

    private int offset=0;
    private final int maxMemroySize = 4;
    Frame[]Memo=new Frame[4];
    public Memory() {
        for(int i=0;i<4;i++){
            Memo[i]=new Frame(-1);
        }
    }


    public boolean write(int offset,int PhysicalAddress,int ID,String data,Process e) {
        if(Memo[PhysicalAddress+offset].getId()<0){
            Memo[PhysicalAddress+offset]=new Frame(ID,data,offset);
            System.out.println("this process added in  frame "+(PhysicalAddress)+" offset "+offset);
            return true;
        }
        else {
            boolean b = false;
            int x = 0;
            for (int i = 0; i < 4; i++) {
                if (Memo[i].getId() < 0) {
                    b = true;
                    x = i;
                    break;
                }
            }
            if (b == false) //if memory is exceeded
                return false;
            else {
                offset=x%2;
                Memo[x] = new Frame(ID, data, x%2);//otherwise add proccess to memory normally.
                e.setPhysicalAddress(x/2);
                e.offset=x%2;
                System.out.println("this process added in  frame " + e.getPhysicalAddress() + " offset " + e.offset);

                return true;

            }}
    }

    public String read(int id){ //get data from proccess with given ID.
        for (Frame obj : Memo)
            if (obj.getId() == id)
                return obj.read();

        return "";
    }

    public void del(int id){ //find process with given ID and delete it from the memory.
        for(int i = 0 ; i < 4; i++)
            if(Memo[i].getId() == id){
                Memo[i]=new Frame(-1);
                return;
            }
    }

    public void replace(int ID, String data,Process e){ //replaces the 1st (oldest) proccess with a fresh proccess.
        int min=100;int oldest = 0;
        for(int j=0;j<4;j++){
            if(Memo[j].getId()<min){
                min=Memo[j].getId();
                oldest=j;

            }
        }
        System.out.println("The process with id  "+min+"is transfered to virtual memory now ");
        Memo[oldest]=new Frame(-1);
        e.setPhysicalAddress(oldest/2);
        e.offset=oldest%2;

        write(e.offset,e.getPhysicalAddress(),ID,data,e);// in general cases write the new proccess to memory.
    }}
package sfe.os;

import java.util.LinkedList;


class Frame {

    private int id;
    private String data;

    Frame(int id,String data){
        this.id = id;
        this.data = data;
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

    private final int maxMemroySize = 4;
    LinkedList<Frame> Memo; //LinkedList to store the frames

    public Memory() {
        Memo = new LinkedList<>();
    }

    public boolean write(int ID,String data) {
        if (Memo.size() >= maxMemroySize) //if memory is exceeded
            return false;

        Memo.add(new Frame(ID,data)); //otherwise add proccess to memory normally.
        return true;
    }

    public String read(int id){ //get data from proccess with given ID.
        for (Frame obj : Memo)
            if (obj.getId() == id)
                return obj.read();

        return "";
    }

    public void del(int id){ //find process with given ID and delete it from the memory.
        for(int i = 0 ; i < Memo.size(); i++)
            if(Memo.get(i).getId() == id){
                Memo.remove(i);
                return;
            }
    }

    public void replace(int ID, String data){ //replaces the 1st (oldest) proccess with a fresh proccess.
        if (Memo.size() != 0) //if memory isn't empty remove the oldest.
            Memo.removeFirst();
        write(ID,data); //in general cases write the new proccess to memory.
    }
}
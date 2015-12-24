package sfe.os;

import java.util.LinkedList;
import java.util.concurrent.*;
import java.util.Timer;
import java.util.TimerTask;
class Process {
    private String type;
    private int duration=0;
    private String data;
    private int id;
    private int LogicalAddress=0;
    private int physicalAddress;
    int offset=0;
    int size=0;

    public Process(int id) {
        this.id=id;
    }

    public void setId(int id)
    {
        this.id=id;
    }
    public int getId()
    {
        return  id;
    }
    public void setVirtualAddress(int virtualAddress)
    {
        this.LogicalAddress=virtualAddress;
    }
    public int getVirtualAddress()
    {
        return  LogicalAddress;
    }

    public void setPhysicalAddress(int PhysicalAddress)
    {
        this.physicalAddress=PhysicalAddress;
    }
    public int getPhysicalAddress()
    {
        return physicalAddress;
    }

    public Process( String type) {
        this.type=type;
    }


    public void setDuration(int duration) {
        this.duration = duration;
    }
    public int getDuration() {
        return duration;
    }

    public String getData() {
        return data;
    }
    public void setData(String data){this.data=data;}
    public String getType() {
        return type;
    }

}

public class CPU  {
    int id=0;
    static int k = 0;
    int quantum = 10;
    int index=0;
    Memory memory;
    int LogicalAddressPage=0;
    int offset=0;
    public static LinkedList<Process> list=new LinkedList<>() ;
    public CPU () {
        this.memory = new Memory();

    }

    public  Process getProcess(int id)
    {
        for(int i=0;i<list.size();i++)
        {
            if(list.get(i).getId()==id)
            {
                return  list.get(i);
            }
        }
        return new Process(id);
    }
    public void addProcess(Process e) {

        if(LogicalAddressPage<3){
            if(offset>1){
                e.setVirtualAddress(++LogicalAddressPage);
                e.offset=0;
                offset=1;
            }
            else{
                e.setVirtualAddress(LogicalAddressPage);
                e.offset=offset++;
            }
            switch (e.getVirtualAddress()) {
                case 0:
                    System.out.println("this process has avirtual address is 00" + e.offset);
                    break;
                case 1:
                    System.out.println("this process has avirtual address is 01" + e.offset);
                    break;
                case 2:
                    System.out.println("this process has avirtual address is 10" + e.offset);
                    break;
                case 3:
                    System.out.println("this process has avirtual address is 11" + e.offset);
                    break;

            }
            e.setId(id++);
            Mapper(e);
        }
        else
        {
            System.out.println("Error memory full");
        }

    }
    public void RR_Schedule() {
        int delay = 2000;
        int period = 2000;
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if(!list.isEmpty()){
                    if(k>=list.size())
                    {
                        k=0;
                    }
                    index=k;
                    System.out.println("running process "+list.get(k++).getId());}

            }
        }, delay, period);

    }


    public void RemoveProcess(int id)
    {
        memory.del(id);
        list.remove(getProcess(id));
        System.out.println("the number of process running in cpu now is "+list.size());
    }


    public void Mapper( Process e){
        if (memory.write(e.offset,e.getPhysicalAddress(),e.getId(), e.getData(),e)){
            if (!list.isEmpty()) {
                list.add((index + 1), e);
            } else {
                list.add(e);
            }
            k = list.indexOf(e);

        } else {

            memory.replace(e.getId(), e.getData(),e);
            if (!list.isEmpty()) {
                list.add((index + 1), e);
            } else {
                list.add(e);
            }}



        System.out.println("process " + e.getId() + " is added in Ram and "+"it's Type "+e.getType());
    }
}
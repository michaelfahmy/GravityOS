package sfe.os;

import java.util.LinkedList;


class Process {

    private int size;
    private int duration;
    private String data;
    public int id;
    int remaining_time;

    public Process(int size, int duration, int id) {
        this.size = size;
        this.duration = duration;
        remaining_time = duration;
        this.id = id;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getSize() {
        return size;
    }

    public int getDuration() {
        return duration;
    }

    public String getData() {
        return data;
    }
}

public class CPU {
    int index=0;
    Memory memory;
    LinkedList<Process> list=new LinkedList<>() ;
    public CPU () {
        this.memory = new Memory();
    }

    public void addProcess(Process e,int index,LinkedList list) {

        if (memory.write(e.id, e.getData())) {
            if (!list.isEmpty()) {
                list.add((index + 1), e);
            } else {
                list.add(e);
            }

            System.out.println("process " + e.id + " is added ");
        } else {

            memory.replace(e.id, e.getData());
            if (list.isEmpty()) {
                list.add((index + 1), e);
            } else {
                list.add(e);
            }

            System.out.println("process " + e.id + " is added ");
        }
    }
}


class Schedular {

    CPU Cpu;
    int k = 0;
    public int id = 0;
    int count = 0;
    int quantum = 10;
    int index = 0;
    LinkedList<Process> list;

    public Schedular() {
        this.Cpu = new CPU();
        list = new LinkedList<>();
    }

    public void RR_Schedule() {

        Process p1 = new Process(100, 50, ++id);
        Process p2 = new Process(100, 40, ++id);
        Cpu.addProcess(p1,index,list);
        Cpu.addProcess(p2,index,list);

        while (!list.isEmpty()) {
            count++;
            for (int i = k; i < list.size(); i++) {
                index = i;
                if (list.get(i).remaining_time > quantum) {
                    list.get(i).remaining_time -= quantum;

                    System.out.println(" process " + list.get(i).id + "remaining time is " + list.get(i).remaining_time);

                } else {
                    System.out.println("process " + list.get(i).id + " is finished");
                    Cpu.memory.del(list.get(i).id);
                    list.remove(i);
                    i--;
                }
            }
            k = 0;
            if (count == 1) {
                Process p3 = new Process(100, 70, ++id);
                Cpu.addProcess(p3,index,list);
                k = list.indexOf(p3);
            }
        }

    }
}


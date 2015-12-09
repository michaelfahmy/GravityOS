package sfe.os;

import java.util.*;


class Process {

    private int size;
    private int duration;
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
}

public class CPU {

    Schedular s = new Schedular();
    Memory memory = new Memory();

    public CPU() {
        s.list = null;
    }

    void addProcess(Process e) {
        if (memory.Count_free_frame() * memory.getMax_Frame_size() >= e.getSize()) {
            memory.Write(e.id, e.getSize());
            if (!s.list.isEmpty()) {
                s.list.add((s.index + 1), e);
            } else {
                s.list.add(e);
            }

            System.out.println("process " + e.id + " is added ");
        } else {
            System.out.println("memory low");
            s.Wqueue.add(e);
        }
    }
}

class Schedular {

    CPU cpu;
    int k = 0;
    public int id = 0;
    int count = 0;
    int quantum = 10;
    int index = 0;
    Queue<Process> Wqueue = new LinkedList<Process>();
    LinkedList<Process> list = new LinkedList<Process>();

    public Schedular() {
    }

    public void RR_Schedule() {

        Process p1 = new Process(100, 50, ++id);
        Process p2 = new Process(100, 40, ++id);
        cpu.addProcess(p1);
        cpu.addProcess(p2);

        while (!list.isEmpty()) {
            count++;
            for (int i = k; i < list.size(); i++) {
                index = i;
                if (list.get(i).remaining_time > quantum) {
                    list.get(i).remaining_time -= quantum;

                    System.out.println(" process " + list.get(i).id + "remaining time is " + list.get(i).remaining_time);

                } else {
                    System.out.println("process " + list.get(i).id + " is finished");
                    cpu.memory.Del(list.get(i).id);
                    list.remove(i);
                    if (!Wqueue.isEmpty()) {
                        if (cpu.memory.Count_free_frame() * cpu.memory.getMax_Frame_size() >= Wqueue.peek().getSize()) {
                            cpu.addProcess(Wqueue.poll());
                        }
                    }
                }
            }
            k = 0;
            if (count == 1) {
                Process p3 = new Process(100, 70, ++id);
                cpu.addProcess(p3);
                k = list.indexOf(p3);
            }
        }

    }
}

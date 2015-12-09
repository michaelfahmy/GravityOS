package sfe.os;


class Frame {

    private int id;
    private int ProcessSize;
    private int TakenSpace;
    private int FreeSpace;

    //id for procaess id and its size Taken Space to store this process and free space (if needed)

    Frame() {
        //id set to zero as to be used to check for free frames 
        id = 0;
    }
    public void Write(int ID, int Pro_Size) {
        id = ID;
        ProcessSize = Pro_Size;
        TakenSpace = Pro_Size;
        FreeSpace = 40 - TakenSpace;
    }

    public void Del() {
        id = 0;
        TakenSpace = 0;
        FreeSpace = 40;
    }

    public boolean free() {
        return id == 0;
    }

    public int GetId() {
        return id;
    }
}

public class Memory {

    private int Max_Memroy_size = 40;
    private int Max_Frame_size = 40;
    private Frame Memo[] = new Frame[Max_Memroy_size];
    private int id;
    private int ProcessSize;

    public Memory() {
        for (int i = 0; i < Max_Memroy_size; i++) {
            Memo[i] = new Frame();
        }
    }

    public void Write(int ID, int Pro_Size) {
        while (Pro_Size > Max_Frame_size) { // if the process will take more than one frame
            for (int i = 0; i < Max_Memroy_size; i++) {
                if (Memo[i].free()) {
                    Memo[i].Write(ID, Max_Frame_size);
                    Pro_Size -= Max_Frame_size;
                    break;
                }
            }
        }
        for (int i = 0; i < Max_Memroy_size; i++) {// to store ba2y el process 
            if (Memo[i].free()) {
                Memo[i].Write(ID, Pro_Size);
            }
        }
    }

    public void Del(int ID) {
        for (int i = 0; i < Max_Memroy_size; i++) {
            if (Memo[i].GetId() == ID) {
                Memo[i].Del();
            }
        }
    }

    int Count_free_frame() {
        int fram = 0;

        for (int i = 0; i < Max_Memroy_size; i++) {
            if (Memo[i].free()) {
                fram++;
            }
        }
        return fram;
    }

    boolean Isfull(int Pro_Size) {
        for (int i = 0; i < Max_Memroy_size; i++) {
            if (Memo[i].free()) {
                return false;
            }
        }
        return true;
    }

    int getMax_Frame_size() {
        return Max_Frame_size;
    }
}

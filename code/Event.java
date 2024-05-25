import java.time.*;;

class Event{
    protected String name;
    protected LocalDateTime start;
    protected LocalDateTime end;

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setStart(LocalDateTime start){
        this.start = start;
    }

    public LocalDateTime getStart(){
        return start;
    }

    public void setEnd(LocalDateTime end){
        this.end = end;
    }

    public LocalDateTime getEnd(){
        return end;
    }   
}
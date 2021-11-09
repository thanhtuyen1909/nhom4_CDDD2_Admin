package vn.edu.tdc.cddd2.data_models;

public class Payroll {
    private int absent;
    private int allowance;
    private int bonus;
    private int fine;
    private int salary;
    private int total;
    private int workday;

    public int getAbsent() {
        return absent;
    }

    public void setAbsent(int absent) {
        this.absent = absent;
    }

    public int getAllowance() {
        return allowance;
    }

    public void setAllowance(int allowance) {
        this.allowance = allowance;
    }

    public int getBonus() {
        return bonus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }

    public int getFine() {
        return fine;
    }

    public void setFine(int fine) {
        this.fine = fine;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getWorkday() {
        return workday;
    }

    public void setWorkday(int workday) {
        this.workday = workday;
    }

    public Payroll() {
    }

    public Payroll(int absent, int allowance, int bonus, int fine, int salary, int total, int workday) {
        this.absent = absent;
        this.allowance = allowance;
        this.bonus = bonus;
        this.fine = fine;
        this.salary = salary;
        this.total = total;
        this.workday = workday;
    }
}

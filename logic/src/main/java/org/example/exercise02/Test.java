package org.example.exercise02;

public class Test {
    public static void main(String[] args) {
        ListNode listNode1 = new ListNode(2);
        listNode1.next = new ListNode(4);
        listNode1.next.next = new ListNode(3);
        ListNode listNode2 = new ListNode(2);
        listNode2.next = new ListNode(5);
        listNode2.next.next = new ListNode(6);
        ListNode listNode = new Exercise1().addTwoNumbers(listNode1, listNode2);
        //ListNode listNode = new Exercise2().addTwoNumbers(listNode1, listNode2);
        System.out.println("listNode.toString() = " + listNode.toString());
    }
}

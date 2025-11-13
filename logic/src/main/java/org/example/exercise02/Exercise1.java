package org.example.exercise02;

public class Exercise1 {
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode head = new ListNode();
        ListNode tail = head;
        int carry = 0;  // 进位

        while (l1 != null || l2 != null || carry != 0) {
            if (l1 != null) {
                carry += l1.val;
                l1 = l1.next;
            }
            if (l2 != null) {
                carry += l2.val;
                l2 = l2.next;
            }
            ListNode subNode = new ListNode(carry % 10);
            carry /= 10;
            tail.next = subNode;
            tail = subNode;
        }

        return head.next;
    }
}

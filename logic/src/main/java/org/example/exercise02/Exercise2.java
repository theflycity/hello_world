package org.example.exercise02;

public class Exercise2 {

    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode tail = null, head = null;
        int n1 = 0, n2 = 0, sum = 0, carry = 0;
        while (l1 != null || l2 != null || carry > 0) {
            n1 = 0;
            n2 = 0;
            if (l1 != null) {
                l1 = l1.next;
                n1 = l1.val;
            }
            if (l2 != null) {
                l2 = l2.next;
                n2 = l2.val;
            }
            sum = n1 + n2 + carry;
            carry = sum / 10;
            if (head == null) {
                // 第一次循环，创建第一个节点
                head = tail = new ListNode(sum % 10);
            } else {
                // 后续循环，在尾部添加新节点
                tail.next = new ListNode(sum % 10);
                tail = tail.next;
            }
        }
        return head;
    }
}

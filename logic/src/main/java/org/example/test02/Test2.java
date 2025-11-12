package org.example.test02;

public class Test2 {
    public static void main(String[] args) {
        ListNode listNode1 = new ListNode(2);
        listNode1.next = new ListNode(4);
        listNode1.next.next = new ListNode(3);
        ListNode listNode2 = new ListNode(2);
        listNode2.next = new ListNode(5);
        listNode2.next.next = new ListNode(6);
        ListNode listNode = new Test2().addTwoNumbers(listNode1, listNode2);
        System.out.println("listNode.toString() = " + listNode.toString());
    }

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

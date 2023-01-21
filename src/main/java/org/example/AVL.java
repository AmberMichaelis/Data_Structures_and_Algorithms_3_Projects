package org.example;

import java.util.NoSuchElementException;

/**
 * Your implementation of an AVL.
 */
public class AVL<T extends Comparable<? super T>> {

    /*
     * Do not add new instance variables or modify existing ones.
     */
    private AVLNode<T> root;
    private int size;

    /*
     * Do not add a constructor.
     */

    /**
     * Adds the element to the tree.
     * <p>
     * Start by adding it as a leaf like in a regular BST and then rotate the
     * tree as necessary.
     * <p>
     * If the data is already in the tree, then nothing should be done (the
     * duplicate shouldn't get added, and size should not be incremented).
     * <p>
     * Remember to recalculate heights and balance factors while going back
     * up the tree after adding the element, making sure to rebalance if
     * necessary. This is as simple as calling the balance() method on the
     * current node, before returning it (assuming that your balance method
     * is written correctly from part 1 of this assignment).
     *
     * @param data The data to add.
     * @throws java.lang.IllegalArgumentException If data is null.
     */
    public void add(T data) {
        // WRITE YOUR CODE HERE (DO NOT MODIFY METHOD HEADER)!
        if (data == null) {
            throw new IllegalArgumentException("Data can't be null");
        }
        root = addHelper(data, root);
    }

    /**
     * Helper method for add(T data)
     *
     * @param data the data to be added
     * @param node the root of a tree
     * @return node that is balanced
     */
    private AVLNode<T> addHelper(T data, AVLNode<T> node) {
        if (node == null) {
            size++;
            return new AVLNode<>(data);
        }
        int tmp = data.compareTo(node.getData());
        if (tmp < 0) {
            node.setLeft(addHelper(data, node.getLeft()));
        } else if (tmp > 0) {
            node.setRight(addHelper(data, node.getRight()));
        } else {
            return node;
        }
        updateHeightAndBF(node);
        return balance(node);
    }

    /**
     * Removes and returns the element from the tree matching the given
     * parameter.
     * <p>
     * There are 3 cases to consider:
     * 1: The node containing the data is a leaf (no children). In this case,
     * simply remove it.
     * 2: The node containing the data has one child. In this case, simply
     * replace it with its child.
     * 3: The node containing the data has 2 children. Use the successor to
     * replace the data, NOT predecessor. As a reminder, rotations can occur
     * after removing the successor node.
     * <p>
     * Remember to recalculate heights and balance factors while going back
     * up the tree after removing the element, making sure to rebalance if
     * necessary. This is as simple as calling the balance() method on the
     * current node, before returning it (assuming that your balance method
     * is written correctly from part 1 of this assignment).
     * <p>
     * Do NOT return the same data that was passed in. Return the data that
     * was stored in the tree.
     * <p>
     * Hint: Should you use value equality or reference equality?
     *
     * @param data The data to remove.
     * @return The data that was removed.
     * @throws java.lang.IllegalArgumentException If the data is null.
     * @throws java.util.NoSuchElementException   If the data is not found.
     */
    public T remove(T data) {
        // WRITE YOUR CODE HERE (DO NOT MODIFY METHOD HEADER)!
        if (data == null) {
            throw new IllegalArgumentException("Data can't be null");
        }
        AVLNode<T> removed = new AVLNode<>(null);
        root = removeHelper(data, root, removed);
        return removed.getData();
    }

    /**
     * Helper method for remove(T data)
     *
     * @param data    the data to search for
     * @param node    the root node to inspect
     * @param removed the node that will store removed data
     * @return parent node of node that will get removed
     */
    private AVLNode<T> removeHelper(T data,
                                    AVLNode<T> node, AVLNode<T> removed) {
        if (node == null) {
            throw new NoSuchElementException("Data can't be found");
        }
        int tmp = data.compareTo(node.getData());
        if (tmp < 0) {
            node.setLeft(removeHelper(data, node.getLeft(), removed));
        } else if (tmp > 0) {
            node.setRight(removeHelper(data, node.getRight(), removed));
        } else {
            size--;
            removed.setData(node.getData());
            if (node.getLeft() == null) {
                return node.getRight();
            } else if (node.getRight() == null) {
                return node.getLeft();
            } else {
                AVLNode<T> child = new AVLNode<>(null);
                node.setRight(successorHelper(node.getRight(), child));
                node.setData(child.getData());
            }
        }
        updateHeightAndBF(node);
        return balance(node);
    }

    /**
     * Finds successor of node
     *
     * @param node  the node to inspect
     * @param child the child of a node that will be removed
     * @return successor node of an element that will be removed
     */
    private AVLNode<T> successorHelper(AVLNode<T> node, AVLNode<T> child) {
        if (node.getLeft() == null) {
            child.setData(node.getData());
            return node.getRight();
        }
        node.setLeft(successorHelper(node.getLeft(), child));
        updateHeightAndBF(node);
        return balance(node);
    }

    /**
     * Updates the height and balance factor of a node using its
     * setter methods.
     * <p>
     * Recall that a null node has a height of -1. If a node is not
     * null, then the height of that node will be its height instance
     * data. The height of a node is the max of its left child's height
     * and right child's height, plus one.
     * <p>
     * The balance factor of a node is the height of its left child
     * minus the height of its right child.
     * <p>
     * This method should run in O(1).
     * You may assume that the passed in node is not null.
     * <p>
     * This method should only be called in rotateLeft(), rotateRight(),
     * and balance().
     *
     * @param currentNode The node to update the height and balance factor of.
     */
    private void updateHeightAndBF(AVLNode<T> node) {
        // COPY YOUR CODE FROM PART 1 OF THE ASSIGNMENT!
        AVLNode<T> lChild = node.getLeft();
        int lHeight, rHeight;
        if (lChild != null) {
            lHeight = lChild.getHeight();
        } else {
            lHeight = -1;
        }
        AVLNode<T> rChild = node.getRight();
        if (rChild != null) {
            rHeight = rChild.getHeight();
        } else {
            rHeight = -1;
        }
        node.setHeight(Math.max(lHeight, rHeight) + 1);
        node.setBalanceFactor(lHeight - rHeight);
    }

    /**
     * Method that rotates a current node to the left. After saving the
     * current's right node to a variable, the right node's left subtree will
     * become the current node's right subtree. The current node will become
     * the right node's left subtree.
     * <p>
     * Don't forget to recalculate the height and balance factor of all
     * affected nodes, using updateHeightAndBF().
     * <p>
     * This method should run in O(1).
     * <p>
     * You may assume that the passed in node is not null and that the subtree
     * starting at that node is right heavy. Therefore, you do not need to
     * perform any preliminary checks, rather, you can immediately perform a
     * left rotation on the passed in node and return the new root of the subtree.
     * <p>
     * This method should only be called in balance().
     *
     * @param currentNode The current node under inspection that will rotate.
     * @return The parent of the node passed in (after the rotation).
     */
    private AVLNode<T> rotateLeft(AVLNode<T> currentNode) {
        // COPY YOUR CODE FROM PART 1 OF THE ASSIGNMENT!
        AVLNode<T> tmp = currentNode.getRight();
        currentNode.setRight(tmp.getLeft());
        tmp.setLeft(currentNode);
        updateHeightAndBF(currentNode);
        updateHeightAndBF(tmp);
        return tmp;
    }

    /**
     * Method that rotates a current node to the right. After saving the
     * current's left node to a variable, the left node's right subtree will
     * become the current node's left subtree. The current node will become
     * the left node's right subtree.
     * <p>
     * Don't forget to recalculate the height and balance factor of all
     * affected nodes, using updateHeightAndBF().
     * <p>
     * This method should run in O(1).
     * <p>
     * You may assume that the passed in node is not null and that the subtree
     * starting at that node is left heavy. Therefore, you do not need to perform
     * any preliminary checks, rather, you can immediately perform a right
     * rotation on the passed in node and return the new root of the subtree.
     * <p>
     * This method should only be called in balance().
     *
     * @param currentNode The current node under inspection that will rotate.
     * @return The parent of the node passed in (after the rotation).
     */
    private AVLNode<T> rotateRight(AVLNode<T> currentNode) {
        // COPY YOUR CODE FROM PART 1 OF THE ASSIGNMENT!
        AVLNode<T> tmp = currentNode.getLeft();
        currentNode.setLeft(tmp.getRight());
        tmp.setRight(currentNode);
        updateHeightAndBF(currentNode);
        updateHeightAndBF(tmp);
        return tmp;
    }

    /**
     * Method that balances out the tree starting at the node passed in.
     * This method should be called in your add() and remove() methods to
     * facilitate rebalancing your tree after an operation.
     * <p>
     * The height and balance factor of the current node is first recalculated.
     * Based on the balance factor, a no rotation, a single rotation, or a
     * double rotation takes place. The current node is returned.
     * <p>
     * You may assume that the passed in node is not null. Therefore, you do
     * not need to perform any preliminary checks, rather, you can immediately
     * check to see if any rotations need to be performed.
     * <p>
     * This method should run in O(1).
     *
     * @param currentNode The current node under inspection.
     * @return The AVLNode that the caller should return.
     */
    private AVLNode<T> balance(AVLNode<T> currentNode) {
        // COPY YOUR CODE FROM PART 1 OF THE ASSIGNMENT!
        /* First, we update the height and balance factor of the current node. */
        updateHeightAndBF(currentNode);

        if ( /* Condition for a right heavy tree. */ currentNode.getBalanceFactor() < -1) {
            if ( /* Condition for a right-left rotation. */ currentNode.getRight().getBalanceFactor() > 0) {
                currentNode.setRight(rotateRight(currentNode.getRight()));
            }
            currentNode = rotateLeft(currentNode);
        } else if ( /* Condition for a left heavy tree. */ currentNode.getBalanceFactor() > 1) {
            if ( /* Condition for a left-right rotation. */ currentNode.getLeft().getBalanceFactor() < 0) {
                currentNode.setLeft(rotateLeft(currentNode.getLeft()));
            }
            currentNode = rotateRight(currentNode);
        }

        return currentNode;
    }

    /**
     * Returns the root of the tree.
     * <p>
     * For grading purposes only. You shouldn't need to use this method since
     * you have direct access to the variable.
     *
     * @return The root of the tree.
     */
    public AVLNode<T> getRoot() {
        // DO NOT MODIFY THIS METHOD!
        return root;
    }

    /**
     * Returns the size of the tree.
     * <p>
     * For grading purposes only. You shouldn't need to use this method since
     * you have direct access to the variable.
     *
     * @return The size of the tree.
     */
    public int size() {
        // DO NOT MODIFY THIS METHOD!
        return size;
    }
}
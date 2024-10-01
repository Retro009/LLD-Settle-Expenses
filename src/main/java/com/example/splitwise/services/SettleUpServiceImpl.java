package com.example.splitwise.services;

import com.example.splitwise.exceptions.InvalidGroupException;
import com.example.splitwise.exceptions.InvalidUserException;
import com.example.splitwise.models.*;
import com.example.splitwise.repositories.*;
import com.example.splitwise.strategies.SettleUpStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class SettleUpServiceImpl implements SettleUpService{

    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private GroupExpenseRepository groupExpenseRepository;
    @Autowired
    private SettleUpStrategy settleUpStrategy;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ExpenseRepository expenseRepository;

    @Override
    public List<Transaction> settleGroup(long groupId) throws InvalidGroupException {
        Group group = groupRepository.findById(groupId).orElseThrow(()-> new InvalidGroupException("Group Id doesn't exist"));
        List<GroupExpense> groupExpenses = groupExpenseRepository.findByGroup(group);
        if(groupExpenses==null || groupExpenses.isEmpty())
            throw new InvalidGroupException("Group doesn't have any expenses");
        List<Expense> expenses = groupExpenses.stream().map(groupExpense -> groupExpense.getExpense()).collect(Collectors.toList());
        List<Transaction> groupTransactions = settleUpStrategy.settle(expenses);
        return groupTransactions;
    }

    @Override
    public List<Transaction> settleUser(long userId) throws InvalidUserException {
        User user = userRepository.findById(userId).orElseThrow(()-> new InvalidUserException("User Id doesn't exist"));
        List<Expense> expenses = expenseRepository.findNonGroupExpensesForUser(user.getId());

        List<Transaction> transactions = settleUpStrategy.settle(expenses);
        System.out.println(transactions.size());

        List<Transaction> userTransactions = transactions.stream().filter(transaction -> transaction.getPaidFrom().equals(user) || transaction.getPaidTo().equals(user)).collect(Collectors.toList());
        return userTransactions;
    }
}

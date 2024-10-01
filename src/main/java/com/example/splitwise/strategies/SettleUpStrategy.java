package com.example.splitwise.strategies;

import com.example.splitwise.models.Expense;
import com.example.splitwise.models.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public interface SettleUpStrategy {
    List<Transaction> settle(List<Expense> expenses);
}

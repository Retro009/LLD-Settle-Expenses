package com.example.splitwise.strategies;

import com.example.splitwise.models.*;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SettleUpStrategyImpl implements SettleUpStrategy{
    @Override
    public List<Transaction> settle(List<Expense> expenses) {
        List<Transaction> transactionList = new ArrayList<>();
        Map<User, Double> userAmountSettleMap = new HashMap<>();
        for(Expense expense: expenses){
            List<ExpenseUser> expenseUsers = expense.getExpenseUsers();
            for(ExpenseUser expenseUser:expenseUsers){
                User user=expenseUser.getUser();
                double amountTobeSettle = expenseUser.getExpenseType().equals(ExpenseType.OWED)? -1 * expenseUser.getAmount() : 1 * expenseUser.getAmount();
                userAmountSettleMap.put(user, userAmountSettleMap.getOrDefault(user,0d)+amountTobeSettle);
            }
        }
        Queue<Pair<User,Double>> whoHasToPay = new PriorityQueue<>(Comparator.comparingDouble(Pair::getSecond));
        Queue<Pair<User,Double>> whoHasToReceive = new PriorityQueue<>((pair1,pair2)-> (int)(pair2.getSecond()-pair1.getSecond()));

        for(Map.Entry<User,Double> entry:userAmountSettleMap.entrySet()){
            User user = entry.getKey();
            Double amount = entry.getValue();
            if(amount<0)
                whoHasToPay.add(Pair.of(user,amount));
            else
                whoHasToReceive.add(Pair.of(user,amount));
        }

        while (!whoHasToPay.isEmpty() && !whoHasToReceive.isEmpty()){
            Pair<User,Double> payee = whoHasToPay.poll();
            Pair<User, Double> receiver = whoHasToReceive.poll();
            Transaction transaction = new Transaction();
            transaction.setPaidFrom(payee.getFirst());
            transaction.setPaidTo(receiver.getFirst());
            if(payee.getSecond() > receiver.getSecond()){
                transaction.setAmount(receiver.getSecond());
                payee = Pair.of(payee.getFirst(),payee.getSecond() - receiver.getSecond());
                whoHasToPay.offer(payee);
            } else if (payee.getSecond() < receiver.getSecond()) {
                transaction.setAmount(Math.abs(payee.getSecond()));
                receiver = Pair.of(receiver.getFirst(),receiver.getSecond() - payee.getSecond());
                whoHasToReceive.offer(receiver);
            } else {
                transaction.setAmount(payee.getSecond());
            }
            transactionList.add(transaction);
        }
        return transactionList;
    }


}

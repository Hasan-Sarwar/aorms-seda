package com.example.aorms_seda;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class OrderRequestFragment extends Fragment {

    RecyclerView orders;
    GestureDetector orderDetector;
    OrderAdapter orderAdapter;
    ArrayList<Order> orderList;
    ArrayList<RequestsChange> dishes;
    FirebaseFirestore db;

    private kitchenActivity kitchenActivity;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root= inflater.inflate(R.layout.kitchen_orders_fragment,container,false);

        orders = (RecyclerView) root.findViewById(R.id.ordersrcv);
        kitchenActivity = (kitchenActivity) getActivity();
        orderList=new ArrayList<Order>();
        dishes=new ArrayList<>();

        db=FirebaseFirestore.getInstance();
        db.collection("OrderRequest")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {

                                //added a request then get it in the list
                                DocumentSnapshot documentSnapshot = dc.getDocument();
                                final ArrayList<Object>Requests= (ArrayList<Object>) documentSnapshot.get("Requests");
                                DocumentReference orderRef=documentSnapshot.getDocumentReference("orderID");
                                orderRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                        final String orderID=documentSnapshot.getId();
                                        String Status=documentSnapshot.getString("Status");
                                        int serveTime=Math.toIntExact(documentSnapshot.getLong("Time"));
                                        Order order=new Order(orderID,serveTime,null,Status,0);
                                        if(isContain(orderList,orderID)==false)
                                        {
                                            orderList.add(order);
                                            orderAdapter.notifyDataSetChanged();
                                        }
                                        for (Object request:Requests)
                                        {
                                            Map<String,Object> map= (Map<String, Object>) request;
                                            final String itemStatus= (String) map.get("itemStatus");
                                            final String requestType= (String) map.get("type");
                                            DocumentReference foodRef= (DocumentReference) map.get("foodItem");
                                            foodRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    String name=documentSnapshot.getString("Name");
                                                    dishes.add(new RequestsChange(name,requestType,null,orderID,itemStatus));
                                                }
                                            });
                                        }

                                    }
                                });


                            }
                        }

                    }
                });




        setorders();

        return root;

    }

    public void setorders()
    {




        orderDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener()
        {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                //      Toast.makeText(c,"onSingleTap",Toast.LENGTH_SHORT).show();

                int index=0;
                View child = orders.findChildViewUnder(e.getX(), e.getY());
                if(child != null)
                {

                    index= orders.getChildAdapterPosition(child);
                    Order order=orderList.get(index);  //get order
                    ArrayList<RequestsChange>Requests=new ArrayList<>();
                    for(int i=0;i<dishes.size();i++)
                    {
                        if(dishes.get(i).orderId.compareTo(order.getOrderId())==0)
                        {
                            Requests.add(dishes.get(i));
                        }
                    }

                    //open movie activity to show details;
                    Intent i=new Intent(kitchenActivity, RequestChangeActivity.class);
                    i.putExtra("order",order);
                    i.putExtra("requests",Requests);
                    startActivity(i);
                }
                return true;
            }
        }

        );




        orderAdapter = new OrderAdapter(orderList,R.layout.kitchen_order_holder);
        orders.setLayoutManager(new GridLayoutManager(getContext(),2, GridLayoutManager.VERTICAL,false));
        //orders.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.GRID,false));
        orders.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
                orderDetector.onTouchEvent(motionEvent);
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {

            }
        });
        orders.setItemAnimator(new DefaultItemAnimator());

        orders.setAdapter(orderAdapter);
    }

    boolean isContain(ArrayList<Order>orderList,String id)
    {
        for(int i=0;i<orderList.size();i++)
        {
            if(orderList.get(i).orderId==id)
            {
                return true;
            }
        }
        return false;
    }
}

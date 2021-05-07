package com.example.moveit;

import java.util.HashMap;

class VehiculeCreationContent {


    public static class CardVehicule {
        final int position;
        String plate;
        String mark;
        String model;
        String type;

        public String getPlate() {
            return plate;
        }

        void setPlate(String plate) {
            this.plate = plate;
        }

        public String getMark() {
            return mark;
        }

        void setMark(String mark) {
            this.mark = mark;
        }

        public String getModel() {
            return model;
        }

        void setModel(String model) {
            this.model = model;
        }

        public String getType() {
            return type;
        }

        void setType(String type) {
            this.type = type;
        }

        private final boolean deletable;

        CardVehicule(int position) {
            this.position = position;
            deletable = position != 0;

            plate = "plate";
            mark = "mark";
            model = "model";
            type = "type";
        }

        public HashMap<String, String> getData(){
            HashMap<String, String> data = new HashMap<>();
            data.put("plate", plate);
            data.put("mark", mark);
            data.put("model", model);
            data.put("type", type);

            return  data;
        }

        public boolean isDeletable() {
            return deletable;
        }

        @Override
        public String toString() {

            return "CardVehicule: " + position
                    +", plate: " + plate
                    +", mark: " + mark
                    +", model: " + model
                    +", type: " + type;
        }
    }
}

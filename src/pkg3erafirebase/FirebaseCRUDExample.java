package pkg3erafirebase;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import static java.lang.Thread.sleep;

public class FirebaseCRUDExample {
    private FirebaseDatabase firebaseDatabase;

    public static void main(String[] args) throws InterruptedException {
        FirebaseCRUDExample example = new FirebaseCRUDExample();
        example.initFirebase();

        // Escritura (Guardar)
        Item itemToSave = new Item("iphone", 100.156, 10L);
        String newItemKey = example.saveItem(itemToSave);
        
        sleep(5000);

        // Lectura (Leer)
        example.readItem(newItemKey);
        
        sleep(5000);

        // Edición (Actualizar)
        example.updateItem(newItemKey, "iphone X");
        
        sleep(5000);
        
        // Lectura (Leer)
        example.readItem(newItemKey);
        
        sleep(5000);

        // Borrado (Eliminar)
        example.deleteItem(newItemKey);
    }

    private void initFirebase() {
        try {
            FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                    .setDatabaseUrl("https://escrituralecturaedicionborrar-default-rtdb.firebaseio.com/")
                    .setServiceAccount(new FileInputStream(new File("C:\\Users\\FLIA ARENAS CARMONA\\Documents\\GABRIEL\\2023\\SEGUNDO SEMESTRE\\ProgramaciónOrientadaAObjetos\\porfa.json")))
                    .build();
            FirebaseApp.initializeApp(firebaseOptions);
            firebaseDatabase = FirebaseDatabase.getInstance();
            System.out.println("Conexión exitosa a Firebase...");
        } catch (FileNotFoundException ex) {
            System.out.println("Error en la inicialización de Firebase: " + ex.getMessage());
        }
    }

    private String saveItem(Item item) {
        DatabaseReference databaseReference = firebaseDatabase.getReference("/CarpetaPrueba");
        DatabaseReference newItemReference = databaseReference.push();

        // Establece el ID con la clave generada como una cadena
        item.setId(newItemReference.getKey());

        // Guarda el objeto Item en la base de datos Firebase
        newItemReference.setValue(item)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        System.out.println("Registro guardado con éxito. Clave: " + newItemReference.getKey());

                        // Después de que la escritura sea exitosa, realiza la operación de lectura
                        readItem(newItemReference.getKey());
                    } else {
                        System.out.println("Error al guardar el registro: " + task.getException().getMessage());
                    }
                });

        return newItemReference.getKey(); // Devuelve la clave generada para el nuevo objeto Item
    }

    private void readItem(String itemId) {
        DatabaseReference databaseReference = firebaseDatabase.getReference("/CarpetaPrueba/" + itemId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Item item = dataSnapshot.getValue(Item.class);
                if (item != null) {
                    System.out.println("ID: " + itemId + ", Nombre: " + item.getName() + ", Precio: " + item.getPrice());
                } else {
                    System.out.println("No se encontró el registro con ID: " + itemId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error al leer datos: " + databaseError.getMessage());
            }
        });
    }

    private void updateItem(String itemId, String newName) {
        DatabaseReference databaseReference = firebaseDatabase.getReference("/CarpetaPrueba/" + itemId + "/name");
        databaseReference.setValue(newName, (DatabaseError databaseError, DatabaseReference databaseReference1) -> {
            if (databaseError == null) {
                System.out.println("Registro actualizado con éxito.");
            } else {
                System.out.println("Error al actualizar el registro: " +
                        databaseError.getMessage());
            }
        });
    }

    private void deleteItem(String itemId) {
        DatabaseReference databaseReference = firebaseDatabase.getReference("/CarpetaPrueba/" + itemId);
        databaseReference.removeValue((DatabaseError databaseError, DatabaseReference databaseReference1) -> {
            if (databaseError == null) {
                System.out.println("Registro eliminado con éxito.");
            } else {
                System.out.println("Error al eliminar el registro: " + databaseError.getMessage());
            }
        });
    }
}
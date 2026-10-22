package com.example.domain.model

data class EverydayPhrase(
  val id: Int,
  val spanishText: String,
  val category: String,
  val englishHint: String = ""
)

object EverydayPhrasesCatalog {
  val phrases: List<EverydayPhrase> = listOf(
    // 1-10: Mañana y desayuno en casa
    EverydayPhrase(
      id = 1,
      spanishText = "Buenos días, ¿dormiste bien anoche?",
      category = "Mañana",
      englishHint = "Good morning, did you sleep well last night?"
    ),
    EverydayPhrase(
      id = 2,
      spanishText = "¿A qué hora vamos a desayunar hoy?",
      category = "Mañana",
      englishHint = "What time are we having breakfast today?"
    ),
    EverydayPhrase(
      id = 3,
      spanishText = "Voy a preparar una taza de café caliente.",
      category = "Desayuno",
      englishHint = "I am going to make a hot cup of coffee."
    ),
    EverydayPhrase(
      id = 4,
      spanishText = "¿Me pasas la sal y la pimienta, por favor?",
      category = "Comida",
      englishHint = "Can you pass me the salt and pepper, please?"
    ),
    EverydayPhrase(
      id = 5,
      spanishText = "Tengo que lavar los platos antes de salir.",
      category = "Cocina",
      englishHint = "I have to wash the dishes before leaving."
    ),
    EverydayPhrase(
      id = 6,
      spanishText = "¿Dónde dejé las llaves de la casa?",
      category = "Hogar",
      englishHint = "Where did I leave the house keys?"
    ),
    EverydayPhrase(
      id = 7,
      spanishText = "Por favor, apaga las luces de la sala.",
      category = "Hogar",
      englishHint = "Please turn off the living room lights."
    ),
    EverydayPhrase(
      id = 8,
      spanishText = "Voy a barrer y trapear el piso de la cocina.",
      category = "Limpieza",
      englishHint = "I am going to sweep and mop the kitchen floor."
    ),
    EverydayPhrase(
      id = 9,
      spanishText = "¿Puedes ayudarme a sacar la basura al contenedor?",
      category = "Limpieza",
      englishHint = "Can you help me take the trash out to the bin?"
    ),
    EverydayPhrase(
      id = 10,
      spanishText = "La comida ya está servida en la mesa.",
      category = "Comida",
      englishHint = "The food is already served on the table."
    ),

    // 11-20: Compras, mandados y tareas cotidianas
    EverydayPhrase(
      id = 11,
      spanishText = "Necesito ir al supermercado a comprar leche, huevos y pan.",
      category = "Compras",
      englishHint = "I need to go to the supermarket to buy milk, eggs, and bread."
    ),
    EverydayPhrase(
      id = 12,
      spanishText = "Se acabó el agua potable, tenemos que comprar otro bidón.",
      category = "Hogar",
      englishHint = "We ran out of drinking water, we have to buy another jug."
    ),
    EverydayPhrase(
      id = 13,
      spanishText = "¿Viste mi teléfono celular? No lo encuentro por ninguna parte.",
      category = "Cotidiano",
      englishHint = "Did you see my cell phone? I cannot find it anywhere."
    ),
    EverydayPhrase(
      id = 14,
      spanishText = "Voy a doblar la ropa limpia y guardarla en el armario.",
      category = "Ropa",
      englishHint = "I will fold the clean clothes and put them in the closet."
    ),
    EverydayPhrase(
      id = 15,
      spanishText = "¿A qué hora llega el cartero con el paquete que pedí?",
      category = "Mandados",
      englishHint = "What time does the mail carrier arrive with the package I ordered?"
    ),
    EverydayPhrase(
      id = 16,
      spanishText = "Hace mucho calor hoy, voy a encender el ventilador.",
      category = "Clima",
      englishHint = "It is very hot today, I am going to turn on the fan."
    ),
    EverydayPhrase(
      id = 17,
      spanishText = "Cierra la ventana porque va a empezar a llover muy fuerte.",
      category = "Clima",
      englishHint = "Close the window because it is going to start raining hard."
    ),
    EverydayPhrase(
      id = 18,
      spanishText = "Voy a tomar una ducha rápida antes de vestirme.",
      category = "Rutina",
      englishHint = "I am going to take a quick shower before getting dressed."
    ),
    EverydayPhrase(
      id = 19,
      spanishText = "¿Quieres que te prepare algo rico de almorzar?",
      category = "Comida",
      englishHint = "Do you want me to make you something delicious for lunch?"
    ),
    EverydayPhrase(
      id = 20,
      spanishText = "El refrigerador está casi vacío, hay que hacer compras esta tarde.",
      category = "Compras",
      englishHint = "The refrigerator is almost empty, we must go shopping this afternoon."
    ),

    // 21-30: Cuidados del hogar, familia y mascotas
    EverydayPhrase(
      id = 21,
      spanishText = "Tengo que regar las plantas del jardín y del balcón.",
      category = "Jardín",
      englishHint = "I have to water the plants in the garden and on the balcony."
    ),
    EverydayPhrase(
      id = 22,
      spanishText = "Por favor, quítate los zapatos antes de entrar a la casa.",
      category = "Hogar",
      englishHint = "Please take off your shoes before entering the house."
    ),
    EverydayPhrase(
      id = 23,
      spanishText = "¿Quién dejó la puerta principal abierta de par en par?",
      category = "Hogar",
      englishHint = "Who left the front door wide open?"
    ),
    EverydayPhrase(
      id = 24,
      spanishText = "El control remoto del televisor no tiene baterías nuevas.",
      category = "Sala",
      englishHint = "The TV remote control does not have fresh batteries."
    ),
    EverydayPhrase(
      id = 25,
      spanishText = "Voy a poner una carga de ropa en la lavadora con detergente.",
      category = "Ropa",
      englishHint = "I will put a load of clothes in the washing machine with detergent."
    ),
    EverydayPhrase(
      id = 26,
      spanishText = "La sopa está muy caliente, déjala enfriar un poco antes de comer.",
      category = "Comida",
      englishHint = "The soup is very hot, let it cool down a bit before eating."
    ),
    EverydayPhrase(
      id = 27,
      spanishText = "¿Me ayudas a tender la cama y cambiar las sábanas limpias?",
      category = "Dormitorio",
      englishHint = "Can you help me make the bed and change the clean sheets?"
    ),
    EverydayPhrase(
      id = 28,
      spanishText = "Voy a ordenar mi escritorio para ponerme a trabajar concentrado.",
      category = "Trabajo",
      englishHint = "I will tidy up my desk to get to work focused."
    ),
    EverydayPhrase(
      id = 29,
      spanishText = "Tenemos que limpiar el microondas porque quedó sucio después de calentar.",
      category = "Cocina",
      englishHint = "We need to clean the microwave because it got dirty after heating."
    ),
    EverydayPhrase(
      id = 30,
      spanishText = "¿Ya le diste de comer y agua fresca al perro esta mañana?",
      category = "Mascota",
      englishHint = "Did you already feed and give fresh water to the dog this morning?"
    ),

    // 31-40: Tarde, descanso y convivencia
    EverydayPhrase(
      id = 31,
      spanishText = "Voy a descansar unos veinte minutos en el sofá.",
      category = "Descanso",
      englishHint = "I am going to rest for about twenty minutes on the couch."
    ),
    EverydayPhrase(
      id = 32,
      spanishText = "Asegúrate de cerrar con llave la puerta principal antes de dormir.",
      category = "Seguridad",
      englishHint = "Make sure to lock the front door before going to sleep."
    ),
    EverydayPhrase(
      id = 33,
      spanishText = "¿Puedes bajarle un poco al volumen de la música?",
      category = "Convivencia",
      englishHint = "Can you turn down the volume of the music a bit?"
    ),
    EverydayPhrase(
      id = 34,
      spanishText = "Estoy buscando mis gafas de lectura, juraba que estaban aquí.",
      category = "Cotidiano",
      englishHint = "I am looking for my reading glasses, I swore they were right here."
    ),
    EverydayPhrase(
      id = 35,
      spanishText = "Vamos a cenar juntos en familia a las ocho de la noche.",
      category = "Cena",
      englishHint = "We are going to have dinner together as a family at eight tonight."
    ),
    EverydayPhrase(
      id = 36,
      spanishText = "Voy a hervir agua para hacerme un té de manzanilla caliente.",
      category = "Cocina",
      englishHint = "I am going to boil water to make myself a hot chamomile tea."
    ),
    EverydayPhrase(
      id = 37,
      spanishText = "La mesa del comedor quedó con manchas de salsa, voy a limpiarla con un trapo.",
      category = "Limpieza",
      englishHint = "The dining table has sauce stains, I will wipe it with a cloth."
    ),
    EverydayPhrase(
      id = 38,
      spanishText = "¿Queda algún postre en el refrigerador o se lo comieron todo?",
      category = "Comida",
      englishHint = "Is there any dessert left in the fridge or did you eat it all?"
    ),
    EverydayPhrase(
      id = 39,
      spanishText = "Hay que cambiar el foco de la lámpara del pasillo porque se quemó.",
      category = "Mantenimiento",
      englishHint = "We need to change the bulb in the hallway lamp because it burned out."
    ),
    EverydayPhrase(
      id = 40,
      spanishText = "Pon los platos sucios en el fregadero para lavarlos más tarde.",
      category = "Cocina",
      englishHint = "Put the dirty dishes in the sink to wash them later."
    ),

    // 41-50: Tarde-noche y fin del día
    EverydayPhrase(
      id = 41,
      spanishText = "Voy a aspirar la alfombra de la sala de estar.",
      category = "Limpieza",
      englishHint = "I am going to vacuum the living room rug."
    ),
    EverydayPhrase(
      id = 42,
      spanishText = "¿Quieres que te sirva un vaso de jugo de naranja natural?",
      category = "Bebida",
      englishHint = "Do you want me to pour you a glass of fresh orange juice?"
    ),
    EverydayPhrase(
      id = 43,
      spanishText = "No encuentro mi billetera, creo que la dejé sobre la mesa de noche.",
      category = "Cotidiano",
      englishHint = "I cannot find my wallet, I think I left it on the nightstand."
    ),
    EverydayPhrase(
      id = 44,
      spanishText = "Voy a planchar esta camisa blanca para la reunión de mañana.",
      category = "Ropa",
      englishHint = "I will iron this white shirt for tomorrow's meeting."
    ),
    EverydayPhrase(
      id = 45,
      spanishText = "Abre las cortinas para que entre la luz del sol a la habitación.",
      category = "Dormitorio",
      englishHint = "Open the curtains so sunlight can enter the room."
    ),
    EverydayPhrase(
      id = 46,
      spanishText = "Tengo que sacar la carne del congelador para que se descongele a tiempo.",
      category = "Cocina",
      englishHint = "I have to take the meat out of the freezer so it thaws in time."
    ),
    EverydayPhrase(
      id = 47,
      spanishText = "Ya terminé de limpiar el baño, quedó impecable y con aroma a lavanda.",
      category = "Limpieza",
      englishHint = "I finished cleaning the bathroom, it was spotless and smelled of lavender."
    ),
    EverydayPhrase(
      id = 48,
      spanishText = "¿Puedes revisar si llegó alguna carta o factura en el buzón?",
      category = "Mandados",
      englishHint = "Can you check if any letter or bill arrived in the mailbox?"
    ),
    EverydayPhrase(
      id = 49,
      spanishText = "Voy a poner la alarma a las seis de la mañana para despertar temprano.",
      category = "Noche",
      englishHint = "I will set the alarm for six in the morning to wake up early."
    ),
    EverydayPhrase(
      id = 50,
      spanishText = "Buenas noches a todos, que tengan un lindo descanso hasta mañana.",
      category = "Noche",
      englishHint = "Good night everyone, have a nice rest until tomorrow."
    )
  )
}

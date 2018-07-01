echo "**********Installazione postgresql..."
apt-get install postgresql;
echo "Installazione completata.************"

echo "\n\n\n**********Imposta password per user postgres (consiglio 'postgres')"
sudo -u postgres psql --command '\password postgres';

echo "\n\n\n**********Creazione database GreenMind"
sudo -u postgres psql --command 'CREATE DATABASE "GreenMind"';
echo "Creato.****************"

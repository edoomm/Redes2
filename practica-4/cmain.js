const contenedor = document.getElementById('contenedor')

const llamarAPI = async () => {
    const res = await fetch('https://rickandmortyapi.com/api/character')
    const data = await res.json()
    const personajes = data.results
    /*
    const result = personajes
        .map((personaje) => {
            return generarTarjeta(personaje)
        })
        .join('')
    */
    const result = personajes
    .map(generarTarjeta)
    .join('')
    contenedor.innerHTML = result
}

const generarTarjeta = ({image, name, species, status, gender}) => {
    console.log(image, name, species, status)
    return `
    <div class="property-card">
        <div class="property-image" style="background-image:url('${image}')">
            <div class="property-image-title">
                <h5>${name}</h5>
            </div>
        </div>
        <div class="property-description">
            <h5> ${name} </h5>
            <h6><b>Species:</b> ${species}</h6>
            <h6><b>Status:</b> ${status}</h6>
            <h6><b>Gender:</b> ${gender}</h6>
        </div>
    </div>
    `
}

llamarAPI()
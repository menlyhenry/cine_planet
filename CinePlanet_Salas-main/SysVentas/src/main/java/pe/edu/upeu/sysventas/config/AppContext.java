package pe.edu.upeu.sysventas.config;

import pe.edu.upeu.sysventas.controller.ClienteController;
import pe.edu.upeu.sysventas.controller.PeliculaController;
import pe.edu.upeu.sysventas.controller.ProductoController;
import pe.edu.upeu.sysventas.controller.SalaController;
import pe.edu.upeu.sysventas.repository.CategoriaRepository;
import pe.edu.upeu.sysventas.repository.ClienteRepository;
import pe.edu.upeu.sysventas.repository.MarcaRepository;
import pe.edu.upeu.sysventas.repository.PeliculaRepository;
import pe.edu.upeu.sysventas.repository.ProductoRepository;
import pe.edu.upeu.sysventas.repository.SalaRepository;
import pe.edu.upeu.sysventas.repository.UnidadMedidaRepository;
import pe.edu.upeu.sysventas.service.ICategoriaService;
import pe.edu.upeu.sysventas.service.IClienteService;
import pe.edu.upeu.sysventas.service.IMarcaService;
import pe.edu.upeu.sysventas.service.IPeliculaService;
import pe.edu.upeu.sysventas.service.IProductoService;
import pe.edu.upeu.sysventas.service.ISalaService;
import pe.edu.upeu.sysventas.service.IUnidadMedidaService;
import pe.edu.upeu.sysventas.service.impl.CategoriaServiceImp;
import pe.edu.upeu.sysventas.service.impl.ClienteServiceImp;
import pe.edu.upeu.sysventas.service.impl.MarcaServiceImp;
import pe.edu.upeu.sysventas.service.impl.PeliculaServiceImp;
import pe.edu.upeu.sysventas.service.impl.ProductoServiceImp;
import pe.edu.upeu.sysventas.service.impl.SalaServiceImp;
import pe.edu.upeu.sysventas.service.impl.UnidadMedidaServiceImp;

import java.util.HashMap;
import java.util.Map;

public class AppContext {
    private static AppContext instance;
    private final Map<Class<?>, Object> contenedor = new HashMap<>();

    public static synchronized AppContext getInstance() {
        if (instance == null) instance = new AppContext();
        return instance;
    }

    private AppContext() {
        registrarRepositorios();
        registrarServicios();
        registrarControladores();
    }

    private void registrarRepositorios() {
        registrar(CategoriaRepository.class, new CategoriaRepository());
        registrar(MarcaRepository.class, new MarcaRepository());
        registrar(UnidadMedidaRepository.class, new UnidadMedidaRepository());
        registrar(ProductoRepository.class, new ProductoRepository());
        registrar(SalaRepository.class, new SalaRepository());
        registrar(ClienteRepository.class, new ClienteRepository());
        registrar(PeliculaRepository.class, new PeliculaRepository());
    }

    private void registrarServicios() {
        registrar(ICategoriaService.class, new CategoriaServiceImp(getBean(CategoriaRepository.class)));
        registrar(IMarcaService.class, new MarcaServiceImp(getBean(MarcaRepository.class)));
        registrar(IProductoService.class, new ProductoServiceImp(getBean(ProductoRepository.class)));
        registrar(IUnidadMedidaService.class, new UnidadMedidaServiceImp(getBean(UnidadMedidaRepository.class)));
        registrar(ISalaService.class, new SalaServiceImp(getBean(SalaRepository.class)));
        registrar(IClienteService.class, new ClienteServiceImp(getBean(ClienteRepository.class)));
        registrar(IPeliculaService.class, new PeliculaServiceImp(getBean(PeliculaRepository.class)));
    }

    private void registrarControladores() {
        registrar(SalaController.class, new SalaController(getBean(ISalaService.class)));
        registrar(ClienteController.class, new ClienteController(getBean(IClienteService.class)));
        registrar(PeliculaController.class, new PeliculaController(getBean(IPeliculaService.class)));
        registrar(ProductoController.class, new ProductoController(getBean(IProductoService.class)));
    }

    private void registrar(Class<?> tipo, Object bean) {
        contenedor.put(tipo, bean);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> tipo) {
        Object bean = contenedor.get(tipo);
        if (bean == null) {
            bean = contenedor.values().stream()
                    .filter(b -> tipo.isAssignableFrom(b.getClass()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Bean no encontrado: " + tipo.getName()));
        }
        return (T) bean;
    }
}
